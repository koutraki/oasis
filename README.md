# oasis
DORIS is a system that given a knowledge base and a Web service function it deduces the schema mapping and the transformation function.
As input it requires a knowledge base, the url of a Web service and the input type that the Web service expects. 
The central idea is to probe the Web service with a few sample inputs that are entities of the knowledge base and analyze the overlap of the XML call result with the knowledge base in order to deduce the alignments. 

The main parts of this system are the following:

1. Probing: The service is called with several entities from the KB. The result is a set of sample call results. 
2. Path Alignment: Discover root-to-text nodes paths in the call results. Discover input-to-literals paths in the KB. Align the paths in the call results with the paths from the KB. 
3. Entity and Property Discovery: Identify the entities from the KB and their properties that are encoded in the call results. Find the nodes, respective the paths in the call results that correspond to them. 
4. Transformation Function: Build a transformation function as an XSLT script. 

For more details about the project please see [1].

[1] Maria Koutraki, Dan Vodislav, Nicoleta Preda: Deriving Intensional Descriptions for Web Services. CIKM 2015: 971-980
