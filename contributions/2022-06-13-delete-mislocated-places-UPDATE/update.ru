PREFIX geojson: <https://purl.org/geojson/vocab#>
PREFIX ncp:     <http://n2t.net/ark:/39333/ncg/place/>
PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>

DELETE {
  ?place rdfs:seeAlso ?uri ; geojson:geometry ?geometry .
}
WHERE {
  VALUES ?place {
    ncp:NCG00050
    ncp:NCG00269
    ncp:NCG00287
    ncp:NCG00519
    ncp:NCG00551
    ncp:NCG00556
    ncp:NCG00739
    ncp:NCG00773
    ncp:NCG00850
    ncp:NCG00853
    ncp:NCG00894
    ncp:NCG00993
    ncp:NCG01002
    ncp:NCG01025
    ncp:NCG01069
    ncp:NCG01186
    ncp:NCG01350
    ncp:NCG01396
    ncp:NCG01420
    ncp:NCG01421
    ncp:NCG01935
    ncp:NCG01951
    ncp:NCG02057
    ncp:NCG02074
    ncp:NCG02189
    ncp:NCG02229
    ncp:NCG02813
    ncp:NCG02861
    ncp:NCG02867
    ncp:NCG02955
    ncp:NCG02968
    ncp:NCG02984
    ncp:NCG03026
    ncp:NCG03178
    ncp:NCG03275
    ncp:NCG03460
    ncp:NCG03485
    ncp:NCG03643
    ncp:NCG04092
    ncp:NCG04100
    ncp:NCG04259
    ncp:NCG05306
    ncp:NCG05310
    ncp:NCG05510
    ncp:NCG05922
    ncp:NCG06145
    ncp:NCG06192
    ncp:NCG06320
    ncp:NCG06767
    ncp:NCG07334
    ncp:NCG07342
    ncp:NCG07352
    ncp:NCG07358
    ncp:NCG07453
    ncp:NCG07597
    ncp:NCG07756
    ncp:NCG07817
    ncp:NCG08132
    ncp:NCG08239
    ncp:NCG08504
    ncp:NCG08526
    ncp:NCG08866
    ncp:NCG08977
    ncp:NCG09064
    ncp:NCG09098
    ncp:NCG09568
    ncp:NCG09630
    ncp:NCG10106
    ncp:NCG10275
    ncp:NCG10745
    ncp:NCG10796
    ncp:NCG10805
    ncp:NCG10832
    ncp:NCG11451
    ncp:NCG11499
    ncp:NCG11692
    ncp:NCG11839
    ncp:NCG11879
    ncp:NCG12572
    ncp:NCG12843
    ncp:NCG12849
    ncp:NCG13032
    ncp:NCG13038
    ncp:NCG13064
    ncp:NCG13146
    ncp:NCG13379
    ncp:NCG13388
    ncp:NCG14096
    ncp:NCG15029
    ncp:NCG15057
    ncp:NCG15139
    ncp:NCG15470
    ncp:NCG15629
    ncp:NCG16194
    ncp:NCG16221
    ncp:NCG16269
    ncp:NCG16270
    ncp:NCG16273
    ncp:NCG16362
    ncp:NCG16574
  }

  ?place rdfs:seeAlso ?uri ; geojson:geometry ?geometry .

  FILTER (STRSTARTS(STR(?uri), "https://sws.geonames.org/"))
}
