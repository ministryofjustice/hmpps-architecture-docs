# hmpps-architecture-docs

Experimental tooling for generating architecture diagrams with code.

The basic premise is to define a metatdata schema for our products, 
populate it as yaml files, and write code to generate a consistent set of diagrams,
pulling data from external services as required.

## Tooling

### HMPPSArchitecture

It's a Java app. To run locally, run HMPPSArchitecture:main via the IDE or command line

You'll need to install graphviz, e.g. 

```sudo apt install graphviz```

And you'll need a Structurizr account. Export these environment variables:

* STRUCTURIZR_WORKSPACE_ID
* STRUCTURIZR_API_KEY
* STRUCTURIZR_API_SECRET
