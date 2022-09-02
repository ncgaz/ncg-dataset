PREFIX dcterms: <http://purl.org/dc/terms/>
PREFIX ncg:     <http://n2t.net/ark:/39333/ncg/>
PREFIX nct:     <http://n2t.net/ark:/39333/ncg/type#>
PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>
PREFIX skos:    <http://www.w3.org/2004/02/skos/core#>

DELETE {
  ?reference
    dcterms:type nct:reference ;
    skos:note ?note ;
    skos:prefLabel ?altLabel .
}
INSERT {
  ?place skos:altLabel ?altLabel .
}
WHERE {
  ?reference
    dcterms:type nct:reference ;
    skos:note ?note ;
    skos:prefLabel ?altLabel .

  ?place skos:prefLabel ?prefLabel .

  FILTER ( CONCAT("See ", ?prefLabel, ".") = ?note )
  FILTER NOT EXISTS { ?place dcterms:type nct:reference }
}
