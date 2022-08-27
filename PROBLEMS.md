# offensive labels

create ncv:offensiveLabel, make it a subproperty of skos:hiddenLabel,
and use it for offensive placenames.

# json-ld

Should create a JSON Schema for documenting the structure.

# types

Some things that are typed as waterways should actually have other
types, according to their descriptions.

# authority mappings

VIAF URLs actually point to the "concepts", they shoudl point to the
"real world objects". In other words we should replace the weird URLs
like <http://viaf.org/viaf/sourceID/LC%7Cn++93023132#skos:Concept>
with normal ones like <http://viaf.org/viaf/140867911>.

Need to double-check the authority mappings for the following places,
as their ids appear multiple times in
[contributions/2020-04-17-external-authorities-ADD/data.csv](contributions/2020-04-17-external-authorities-ADD/data.csv).

* NCG00361
* NCG02346
* NCG02984
* NCG03059
* NCG14071
* NCG14299
* NCG15281
* NCG16642

# encoding / spelling issues

* NCG03792
* NCG01290

# references

References should be turned into skos:altLabels.

# missing towns/cities

* Flat Rock, Henderson County
* Franklin, Macon County
* Mount Pleasant, Cabarrus County
* Mt. Olive, Duplin County
* Grabtown, Johnston County (Ava Gardner's birthplace)

# multiple counties mentioned in description, but only one linked

# miscellany

* link precincts to their present-day counties with ncv:succeededBy
* link Archdale, Pamptecough, and Wickham precincts to Bath County
* capitalization of directions in North Toe River description
* link waterways to https://waterdata.usgs.gov/ monitoring locations

# wrongish locations

Exception to the rule that points should be within counties:

* NCG02559 Cape Lookout Shoals
