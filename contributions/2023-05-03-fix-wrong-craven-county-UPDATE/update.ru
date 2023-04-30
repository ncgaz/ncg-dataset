PREFIX dcterms: <http://purl.org/dc/terms/>
PREFIX geojson: <https://purl.org/geojson/vocab#>
PREFIX ncp:     <http://n2t.net/ark:/39333/ncg/place/>
PREFIX nct:     <http://n2t.net/ark:/39333/ncg/type#>
PREFIX ncv:     <http://n2t.net/ark:/39333/ncg/vocab#>
PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>

DELETE {
  ncp:NCG03747 rdfs:seeAlso ?other .
  ncp:NCG03747 geojson:geometry ?geometry .
  ncp:NCG03747 dcterms:type nct:county .
  ?place ncv:county ncp:NCG03747 .
}
INSERT {
  ncp:NCG20482 rdfs:seeAlso ?other .
  ncp:NCG20482 geojson:geometry ?geometry .
  ncp:NCG03747 dcterms:type nct:formerCounty .
  ?place ncv:county ncp:NCG20482 .
}
WHERE {
  ncp:NCG03747 rdfs:seeAlso ?other .
  ncp:NCG03747 geojson:geometry ?geometry .
  ?place ncv:county ncp:NCG03747 .
}
