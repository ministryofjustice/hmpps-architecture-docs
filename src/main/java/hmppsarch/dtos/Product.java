package hmppsarch.dtos;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private String name;
    private String description;
    private String technology;
    private String dev_team;
    private List<Dependency> functional = new ArrayList<>();
    private List<Dependency> authentication = new ArrayList<>();
    private List<String> git_repos = new ArrayList<String>();
    private List<String> documentation_sites = new ArrayList<>();
    private List<String> snyk_projects = new ArrayList<>();
    private List<QualityMeasure> qualityMeasures = new ArrayList<>();
    private String languages = "Unknown";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public String getDev_team() {
        return dev_team;
    }

    public void setDev_team(String dev_team) {
        this.dev_team = dev_team;
    }

    public List<Dependency> getFunctional() {
        return functional;
    }

    public void setFunctional(List<Dependency> functional) {
        this.functional = functional;
    }

    public List<Dependency> getAuthentication() {
        return authentication;
    }

    public void setAuthentication(List<Dependency> authentication) {
        this.authentication = authentication;
    }

    public List<String> getGit_repos() {
        return git_repos;
    }

    public void setGit_repos(List<String> git_repos) {
        this.git_repos = git_repos;
    }

    public List<String> getDocumentation_sites() {
        return documentation_sites;
    }

    public void setDocumentation_sites(List<String> documentation_sites) {
        this.documentation_sites = documentation_sites;
    }

    public List<String> getSnyk_projects() {
        return snyk_projects;
    }

    public void setSnyk_projects(List<String> snyk_projects) {
        this.snyk_projects = snyk_projects;
    }

    public List<QualityMeasure> getQualityMeasures() {
        return qualityMeasures;
    }

    public void setQualityMeasures(List<QualityMeasure> qualityMeasures) {
        this.qualityMeasures = qualityMeasures;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }
}
