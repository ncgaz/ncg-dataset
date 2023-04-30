PREFIX dcterms: <http://purl.org/dc/terms/>
PREFIX ncg:     <http://n2t.net/ark:/39333/ncg/>
PREFIX nct:     <http://n2t.net/ark:/39333/ncg/type#>
PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>
PREFIX skos:    <http://www.w3.org/2004/02/skos/core#>
PREFIX ncv:     <http://n2t.net/ark:/39333/ncg/vocab#>

INSERT {
  ?place skos:altLabel ?altLabel .
  ?reference ncv:remove true .
}
WHERE {
  ?reference
    dcterms:type nct:reference ;
    skos:note ?refNote ;
    skos:prefLabel ?altLabel .

  ?place
    skos:prefLabel ?prefLabel ;
    skos:note ?placeNote .

  FILTER ( CONCAT("See ", ?prefLabel, ".") = ?refNote )
  FILTER ( REGEX(?placeNote, CONCAT(".*", ?altLabel, ".*")) )
  FILTER NOT EXISTS { ?place dcterms:type nct:reference }
}
