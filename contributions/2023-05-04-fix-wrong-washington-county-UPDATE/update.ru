PREFIX dcterms: <http://purl.org/dc/terms/>
PREFIX geojson: <https://purl.org/geojson/vocab#>
PREFIX ncp:     <http://n2t.net/ark:/39333/ncg/place/>
PREFIX nct:     <http://n2t.net/ark:/39333/ncg/type#>
PREFIX ncv:     <http://n2t.net/ark:/39333/ncg/vocab#>
PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>

DELETE {
  ncp:NCG15917 rdfs:seeAlso ?other .
  ncp:NCG15917 geojson:geometry ?geometry .
  ncp:NCG15917 dcterms:type nct:county .
  ?place ncv:county ncp:NCG15917 .
}
INSERT {
  ncp:NCG23199 rdfs:seeAlso ?other .
  ncp:NCG23199 geojson:geometry ?geometry .
  ncp:NCG15917 dcterms:type nct:formerCounty .
  ?place ncv:county ncp:NCG23199 .
}
WHERE {
  ncp:NCG15917 rdfs:seeAlso ?other .
  ncp:NCG15917 geojson:geometry ?geometry .
  ?place ncv:county ncp:NCG15917 .
}
