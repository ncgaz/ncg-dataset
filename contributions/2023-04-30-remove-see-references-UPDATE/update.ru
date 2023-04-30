PREFIX dcterms: <http://purl.org/dc/terms/>
PREFIX ncg:     <http://n2t.net/ark:/39333/ncg/>
PREFIX nct:     <http://n2t.net/ark:/39333/ncg/type#>
PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>
PREFIX skos:    <http://www.w3.org/2004/02/skos/core#>
PREFIX ncv:     <http://n2t.net/ark:/39333/ncg/vocab#>

DELETE WHERE {
  # remove all triples associated with the reference to remove
  ?reference
    ncv:remove true ;
    dcterms:type nct:reference ;
    ?p ?o .
}
