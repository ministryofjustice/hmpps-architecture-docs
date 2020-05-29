package hmppsarch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.api.StructurizrClientException;
import com.structurizr.documentation.AutomaticDocumentationTemplate;
import com.structurizr.documentation.Format;
import com.structurizr.graphviz.GraphvizAutomaticLayout;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.ContainerView;
import com.structurizr.view.Styles;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.ViewSet;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import hmppsarch.dtos.Product;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class HMPPSArchitecture {

    private static final String HMPPS_ARCH_SYSTEM = "HMPPS architecture system view";
    private static final String HMPPS_ARCH_CONTAINER = "HMPPS architecture container view";
    private static final String HMPPS_ARCH_FUNCTIONAL_DATA_FLOWS = "HMPPS architecture functional data flow view";
    private static final String HMPPS_ARCH_AUTHENTICATION_DATA_FLOWS = "HMPPS architecture authentication data flow view";
    private static final String HMPPS_ARCH_DOCS = "HMPPS architecture docs";

    public static void main(String[] args) throws Exception {
        HMPPSArchitecture hmppsArchitecture = new HMPPSArchitecture();
        hmppsArchitecture.createAllViews();
        hmppsArchitecture.uploadWorkspace();
    }

    private final Workspace workspace;
    private final ModelBuilder modelBuilder;
    private final ViewSet views;
    private final Model model;
    private final GraphvizAutomaticLayout graphviz;

    public HMPPSArchitecture() {
        workspace = new Workspace("HMPPS Architecture", "Currently deployed systems in HMPPS.");
        modelBuilder = new ModelBuilder();
        views = workspace.getViews();
        model = workspace.getModel();
        graphviz = new GraphvizAutomaticLayout();
    }

    private void createAllViews() throws Exception {
        Map<String, Product> products = loadProducts();
        createSystemContextView(model, products);
        createContainerView(model, products);
        createFunctionalDataFlowView(model, products);
        createAuthenticationDataFlowView(model, products);

        createDocuments(model, products);

        uploadWorkspace();
    }

    private void uploadWorkspace() throws StructurizrClientException {
        StructurizrClient structurizrClient = new StructurizrClient(
                System.getenv("STRUCTURIZR_API_KEY"),
                System.getenv("STRUCTURIZR_API_SECRET")
        );
        structurizrClient.putWorkspace(Integer.parseInt(System.getenv("STRUCTURIZR_WORKSPACE_ID")), workspace);
    }

    private void createSystemContextView(Model model, Map<String, Product> products) throws IOException {
        SoftwareSystem softwareSystem = model.addSoftwareSystem(HMPPS_ARCH_SYSTEM, "Current and near-future HMPPS services");
        modelBuilder.buildContainers(softwareSystem, products);
        SystemContextView contextView = views.createSystemContextView(softwareSystem, "System", "HMPPS System Context diagram.");
        contextView.addAllSoftwareSystems();
        contextView.addAllPeople();
    }

    private void createContainerView(Model model, Map<String, Product> products) throws Exception {
        SoftwareSystem softwareSystem = model.addSoftwareSystem(HMPPS_ARCH_CONTAINER, "Current and near-future HMPPS services");
        modelBuilder.buildContainers(softwareSystem, products);
        ContainerView containerView = views.createContainerView(softwareSystem, "Containers", "HMPPS Containers diagram.");
        containerView.addAllContainers();
//        graphviz.apply(containerView); This blows up for some reason
    }

    private void createDocuments(Model model, Map<String, Product> products) throws Exception {
        SoftwareSystem softwareSystem = model.addSoftwareSystem(HMPPS_ARCH_DOCS, "Current and near-future HMPPS services");
        modelBuilder.buildContainers(softwareSystem, products);

        AutomaticDocumentationTemplate template2 = new AutomaticDocumentationTemplate(workspace);
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
        Template temp = cfg.getTemplate("Container.tpl");
        products.entrySet().stream().sorted(Map.Entry.<String, Product>comparingByKey()).forEach(entry -> {
            String name = entry.getKey();
            Product product = entry.getValue();
            Writer writer = new StringWriter();
            Container container = modelBuilder.getContainer(softwareSystem, name);
            String processed;
            try {
                temp.process(product, writer);
                processed = writer.toString();
            } catch (TemplateException e) {
                processed = "Template error";
            } catch (IOException e) {
                processed = "IO error";
            }
            template2.addSection(container.getName(), Format.Markdown, processed);
        });
    }

    private void createFunctionalDataFlowView(Model model, Map<String, Product> products) throws Exception {
        SoftwareSystem softwareSystem = model.addSoftwareSystem(HMPPS_ARCH_FUNCTIONAL_DATA_FLOWS, "Current and near-future HMPPS services");
        modelBuilder.buildFunctionalDataFlows(softwareSystem, products);
        ContainerView containerView = views.createContainerView(softwareSystem, "Functional data flows", "HMPPS Containers diagram.");
        containerView.addAllContainers();
        Styles styles = views.getConfiguration().getStyles();
        styles.addElementStyle("REST API").background("#438dd5").color("#ffffff");
        styles.addElementStyle("Legacy").background("#7f0000").color("#ffffff");
        styles.addElementStyle("Website").background("#007f00").color("#ffffff");
        graphviz.apply(containerView);
    }

    private void createAuthenticationDataFlowView(Model model, Map<String, Product> products) throws Exception {
        SoftwareSystem softwareSystem = model.addSoftwareSystem(HMPPS_ARCH_AUTHENTICATION_DATA_FLOWS, "Current and near-future HMPPS services");
        modelBuilder.buildAuthenticationDataFlows(softwareSystem, products);
        ContainerView containerView = views.createContainerView(softwareSystem, "Authentication data flows", "HMPPS Containers diagram.");
        containerView.addAllContainers();
//        graphviz.apply(containerView); This blows up for some reason
    }

    private Map<String, Product> loadProducts() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        Map<String, Product> products = new HashMap<String, Product>();
        File dir = new File("src/main/resources/products/");
        File[] files = dir.listFiles();
        if (files != null) {
            GitHub github = new GitHubBuilder().withOAuthToken(System.getenv("GITHUB_TOKEN")).build();
            for (File file : files) {
                Product product = mapper.readValue(file, Product.class);
                for (String git_repo : product.getGit_repos()) {
                    GHRepository repo = github.getRepository(String.format("ministryofjustice/%s", git_repo));
                    product.setLanguages(repo.getLanguage());
                }
                products.put(product.getName(), product);
            }
        }
        return products;
    }
}
