# Problems

## merge places

the two Big Pocosins seem to actually be the same place

sort out whether there are 1 or 2 Cedar Island Points

## offensive labels

create ncv:offensiveLabel, make it a subproperty of skos:hiddenLabel,
and use it for offensive placenames.

## fix labels

ncp:NCG06396 pref label from "Gunt, Gun" to "Gunt Inlet"

ncp:NCG05045 remove alt label "Rutherfords Mill"

ncp:NCG09279 change pref label from "Lookout Bight" to "Barden Inlet"

## references

these got fucked up because of the mis-scraped duplicate names

add skos:related links based on "See also" and "which see" refs in place notes

add skos:related links among Fremont, Torhunta, and Nahunta Swamp

add skos:related between Hibriten Mountain and Hen Mountain

add skos:related between Redstone Point and Weir Point

add skos:related between Grants Creek and Frohock Mill

fix the entry for NC which should not be a reference:
<https://www.ncpedia.org/gazetteer/northcarolina>

## craven county and washington county

sort out this mess caused by mis-scraped duplicate names

## split up / regarrange labels

ncp:NCG10500 skos:prefLabel  "Mountain Region, Mountain Section" .
should be "Mountain Region"

fix labels like "Waxhaws, The" -> "The Waxhaws"

## obsolete alt labels

get rid of alternate labels like "Norfolk, County of"

## update descriptions

ncp:NCG02539 skos:note "town in E Haywood County. Post office operated
by the name Forks of Pigeon, 1841-1901. Inc. 1889 as Buford. Name
changed to Pigeon River in 1891 and to Canton in 1893. Named for
Canton, Ohio, source of the steel used in the bridge being built
across Pigeon River. Produces paper. Alt. 2,609." ;

ncp:NCG14159 skos:note "Church of England, Bertie County, est. with
the formation of the county in 1722 and coextensive with it. Named for
the Society for the Propagation of the Gospel. Previously the
territory had been in South West Parish, Chowan County, est. 1715. The
parish had 930 white taxable inhabitants in 1767, described as
\"capable to maintain & willing to receive a Minister.\" Divided in
1727 to form Northwest Parish, which became Northampton County
in 1741. The church for Society Parish was in the Merry Hill section
of the county on Duckenfield land." ;

## descriptions taken from the original NCG should be clearly cited

## add credits to each page

## json-ld

Should create a JSON Schema for documenting the structure.

## types

Some things that are typed as waterways should actually have other
types, according to their descriptions.

Some current communities are incorrectly typed as former communities, e.g. Brindletown.

Some proposed communities are incorrectly typed as actual communities,
e.g. "Stewart's Town" (ncp:NCG14614)

ncp:NCG00387 (Askins Creek) is actually a cove.

add type formerCommunity to "Borough, The" (ncp:NCG01673)

## authority mappings

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

## encoding / spelling issues

* NCG03792
* NCG01290

## missing towns/cities

* Flat Rock, Henderson County
* Franklin, Macon County
* Mount Pleasant, Cabarrus County
* Mt. Olive, Duplin County
* Grabtown, Johnston County (Ava Gardner's birthplace)

## multiple counties mentioned in description, but only one linked

## miscellany

* link precincts to their present-day counties with ncv:succeededBy
* link Archdale, Pamptecough, and Wickham precincts to Bath County
* capitalization of directions in North Toe River description
* link waterways to <https://waterdata.usgs.gov/> monitoring locations

## wrongish locations

Exception to the rule that points should be within counties:

* NCG02559 Cape Lookout Shoals
