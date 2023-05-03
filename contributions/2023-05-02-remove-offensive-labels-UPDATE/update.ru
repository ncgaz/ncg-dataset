PREFIX ncp:  <http://n2t.net/ark:/39333/ncg/place/>
PREFIX skos: <http://www.w3.org/2004/02/skos/core#>

DELETE { ?s ?p ?o } WHERE {
  {
    SELECT ?s ?p ?o WHERE {
      {
        BIND (URI("http://www.w3.org/2004/02/skos/core#prefLabel") AS ?p)
        ?s ?p ?o .
        FILTER(REGEX(?o, "^.*Nigger.*$"))
      }
      UNION
      {
        BIND (URI("http://www.w3.org/2004/02/skos/core#altLabel") AS ?p)
        ?s ?p ?o .
        FILTER(REGEX(?o, "^.*Nigger.*$"))
      }
      UNION
      {
        # unclear that this offensively-named place ever actually existed
        BIND (URI("http://n2t.net/ark:/39333/ncg/place/NCG10832") AS ?s)
        ?s ?p ?o
      }
      UNION
      {
        BIND (URI("http://n2t.net/ark:/39333/ncg/dataset") AS ?s)
        BIND (URI("http://www.w3.org/2000/01/rdf-schema#member") AS ?p)
        # unclear that this offensively-named place ever actually existed
        BIND (URI("http://n2t.net/ark:/39333/ncg/place/NCG10832") AS ?o)
        ?s ?p ?o
      }
    }
  }
}
