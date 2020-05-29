package hmppsarch;

import com.structurizr.model.Container;
import com.structurizr.model.SoftwareSystem;
import hmppsarch.dtos.Dependency;
import hmppsarch.dtos.Product;

import java.util.Map;

public class ModelBuilder {

    public void buildContainers(SoftwareSystem softwareSystem, Map<String, Product> products) {
        addProducts(softwareSystem, products);
    }

    public void buildFunctionalDataFlows(SoftwareSystem softwareSystem, Map<String, Product> products) {
        buildContainers(softwareSystem, products);
        addFunctionalDependencies(softwareSystem, products);
    }

    public void buildAuthenticationDataFlows(SoftwareSystem softwareSystem, Map<String, Product> products) {
        buildContainers(softwareSystem, products);
        addAuthenticationDependencies(softwareSystem, products);
    }

    public void buildComponents(SoftwareSystem softwareSystem, Map<String, Product> products) {
        addProducts(softwareSystem, products);
    }

    private void addProducts(SoftwareSystem softwareSystem, Map<String, Product> products) {
        products.forEach((name, product) -> {
            Container container = softwareSystem.addContainer(product.getName(), product.getDescription(), product.getTechnology());
            container.addTags(product.getTechnology());
        });
    }

    private void addFunctionalDependencies(SoftwareSystem softwareSystem, Map<String, Product> products) {
        products.forEach((name, product) -> {
            Container downstreamContainer = getContainer(softwareSystem, name);
            for (Dependency dependency : product.getFunctional()) {
                Container upstreamContainer = getContainer(softwareSystem, dependency.getDependsOn());
                downstreamContainer.uses(upstreamContainer, dependency.getDescription());
            }
        });
    }

    private void addComponents(SoftwareSystem softwareSystem, Map<String, Product> products) {
        products.forEach((name, product) -> {
            Container container = getContainer(softwareSystem, name);
            for (String git_repo : product.getGit_repos()) {
                container.addComponent(git_repo, "");
            }
        });
    }

    private void addAuthenticationDependencies(SoftwareSystem softwareSystem, Map<String, Product> products) {
        products.forEach((name, product) -> {
            Container downstreamContainer = getContainer(softwareSystem, name);
            for (Dependency dependency : product.getAuthentication()) {
                Container upstreamContainer = getContainer(softwareSystem, dependency.getDependsOn());
                downstreamContainer.uses(upstreamContainer, dependency.getDescription());
            }
        });
    }

    public Container getContainer(SoftwareSystem softwareSystem, String name) {
        Container container = softwareSystem.getContainerWithName(name);
        if (container == null)
            System.out.println(String.format(" Product %s not found", name));
        return  container;
    }

}
