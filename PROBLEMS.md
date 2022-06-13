# json-ld

Should create a JSON Schema for documenting the structure.

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

# former counties

should be a separate type from current counties


# wrongish locations

Interesting cases:

* http://localhost:8000/NCG02559.html
* http://localhost:8000/NCG11422.html

Others:

http://localhost:8000/NCG00050.html
http://localhost:8000/NCG00083.html
http://localhost:8000/NCG00177.html
http://localhost:8000/NCG00185.html
http://localhost:8000/NCG00269.html
http://localhost:8000/NCG00281.html
http://localhost:8000/NCG00287.html
http://localhost:8000/NCG00454.html
http://localhost:8000/NCG00519.html
http://localhost:8000/NCG00551.html
http://localhost:8000/NCG00556.html
http://localhost:8000/NCG00739.html
http://localhost:8000/NCG00773.html
http://localhost:8000/NCG00850.html
http://localhost:8000/NCG00853.html
http://localhost:8000/NCG00894.html
http://localhost:8000/NCG00924.html
http://localhost:8000/NCG00935.html
http://localhost:8000/NCG00993.html
http://localhost:8000/NCG00999.html
http://localhost:8000/NCG01002.html
http://localhost:8000/NCG01025.html
http://localhost:8000/NCG01061.html
http://localhost:8000/NCG01069.html
http://localhost:8000/NCG01157.html
http://localhost:8000/NCG01186.html
http://localhost:8000/NCG01250.html
http://localhost:8000/NCG01268.html
http://localhost:8000/NCG01280.html
http://localhost:8000/NCG01295.html
http://localhost:8000/NCG01350.html
http://localhost:8000/NCG01378.html
http://localhost:8000/NCG01396.html
http://localhost:8000/NCG01420.html
http://localhost:8000/NCG01421.html
http://localhost:8000/NCG01935.html
http://localhost:8000/NCG01951.html
http://localhost:8000/NCG02057.html
http://localhost:8000/NCG02071.html
http://localhost:8000/NCG02074.html
http://localhost:8000/NCG02141.html
http://localhost:8000/NCG02189.html
http://localhost:8000/NCG02229.html
http://localhost:8000/NCG02278.html
http://localhost:8000/NCG02498.html
http://localhost:8000/NCG02800.html
http://localhost:8000/NCG02813.html
http://localhost:8000/NCG02861.html
http://localhost:8000/NCG02867.html
http://localhost:8000/NCG02931.html
http://localhost:8000/NCG02955.html
http://localhost:8000/NCG02968.html
http://localhost:8000/NCG02984.html
http://localhost:8000/NCG03026.html
http://localhost:8000/NCG03047.html
http://localhost:8000/NCG03068.html
http://localhost:8000/NCG03178.html
http://localhost:8000/NCG03198.html
http://localhost:8000/NCG03241.html
http://localhost:8000/NCG03258.html
http://localhost:8000/NCG03275.html
http://localhost:8000/NCG03366.html
http://localhost:8000/NCG03375.html
http://localhost:8000/NCG03382.html
http://localhost:8000/NCG03460.html
http://localhost:8000/NCG03485.html
http://localhost:8000/NCG03587.html
http://localhost:8000/NCG03606.html
http://localhost:8000/NCG03643.html
http://localhost:8000/NCG03767.html
http://localhost:8000/NCG03899.html
http://localhost:8000/NCG03926.html
http://localhost:8000/NCG03959.html
http://localhost:8000/NCG04092.html
http://localhost:8000/NCG04100.html
http://localhost:8000/NCG04168.html
http://localhost:8000/NCG04259.html
http://localhost:8000/NCG04339.html
http://localhost:8000/NCG04528.html
http://localhost:8000/NCG04587.html
http://localhost:8000/NCG04591.html
http://localhost:8000/NCG04656.html
http://localhost:8000/NCG04661.html
http://localhost:8000/NCG04861.html
http://localhost:8000/NCG05003.html
http://localhost:8000/NCG05185.html
http://localhost:8000/NCG05229.html
http://localhost:8000/NCG05298.html
http://localhost:8000/NCG05306.html
http://localhost:8000/NCG05310.html
http://localhost:8000/NCG05510.html
http://localhost:8000/NCG05618.html
http://localhost:8000/NCG05922.html
http://localhost:8000/NCG06018.html
http://localhost:8000/NCG06145.html
http://localhost:8000/NCG06192.html
http://localhost:8000/NCG06320.html
http://localhost:8000/NCG06381.html
http://localhost:8000/NCG06503.html
http://localhost:8000/NCG06543.html
http://localhost:8000/NCG06580.html
http://localhost:8000/NCG06604.html
http://localhost:8000/NCG06626.html
http://localhost:8000/NCG06658.html
http://localhost:8000/NCG06767.html
http://localhost:8000/NCG06816.html
http://localhost:8000/NCG06875.html
http://localhost:8000/NCG07296.html
http://localhost:8000/NCG07334.html
http://localhost:8000/NCG07342.html
http://localhost:8000/NCG07351.html
http://localhost:8000/NCG07352.html
http://localhost:8000/NCG07358.html
http://localhost:8000/NCG07453.html
http://localhost:8000/NCG07485.html
http://localhost:8000/NCG07597.html
http://localhost:8000/NCG07648.html
http://localhost:8000/NCG07756.html
http://localhost:8000/NCG07760.html
http://localhost:8000/NCG07817.html
http://localhost:8000/NCG07898.html
http://localhost:8000/NCG08005.html
http://localhost:8000/NCG08076.html
http://localhost:8000/NCG08132.html
http://localhost:8000/NCG08165.html
http://localhost:8000/NCG08239.html
http://localhost:8000/NCG08275.html
http://localhost:8000/NCG08504.html
http://localhost:8000/NCG08526.html
http://localhost:8000/NCG08604.html
http://localhost:8000/NCG08812.html
http://localhost:8000/NCG08866.html
http://localhost:8000/NCG08893.html
http://localhost:8000/NCG08916.html
http://localhost:8000/NCG08940.html
http://localhost:8000/NCG08950.html
http://localhost:8000/NCG08977.html
http://localhost:8000/NCG08990.html
http://localhost:8000/NCG08999.html
http://localhost:8000/NCG09017.html
http://localhost:8000/NCG09049.html
http://localhost:8000/NCG09064.html
http://localhost:8000/NCG09098.html
http://localhost:8000/NCG09118.html
http://localhost:8000/NCG09133.html
http://localhost:8000/NCG09318.html
http://localhost:8000/NCG09429.html
http://localhost:8000/NCG09568.html
http://localhost:8000/NCG09601.html
http://localhost:8000/NCG09630.html
http://localhost:8000/NCG09850.html
http://localhost:8000/NCG09854.html
http://localhost:8000/NCG10030.html
http://localhost:8000/NCG10106.html
http://localhost:8000/NCG10225.html
http://localhost:8000/NCG10275.html
http://localhost:8000/NCG10607.html
http://localhost:8000/NCG10610.html
http://localhost:8000/NCG10631.html
http://localhost:8000/NCG10715.html
http://localhost:8000/NCG10722.html
http://localhost:8000/NCG10745.html
http://localhost:8000/NCG10796.html
http://localhost:8000/NCG10805.html
http://localhost:8000/NCG10832.html
http://localhost:8000/NCG10938.html
http://localhost:8000/NCG10958.html
http://localhost:8000/NCG10962.html
http://localhost:8000/NCG10971.html
http://localhost:8000/NCG10989.html
http://localhost:8000/NCG10999.html
http://localhost:8000/NCG11038.html
http://localhost:8000/NCG11322.html
http://localhost:8000/NCG11326.html
http://localhost:8000/NCG11420.html
http://localhost:8000/NCG11437.html
http://localhost:8000/NCG11445.html
http://localhost:8000/NCG11451.html
http://localhost:8000/NCG11499.html
http://localhost:8000/NCG11692.html
http://localhost:8000/NCG11839.html
http://localhost:8000/NCG11879.html
http://localhost:8000/NCG12025.html
http://localhost:8000/NCG12052.html
http://localhost:8000/NCG12174.html
http://localhost:8000/NCG12178.html
http://localhost:8000/NCG12414.html
http://localhost:8000/NCG12572.html
http://localhost:8000/NCG12738.html
http://localhost:8000/NCG12760.html
http://localhost:8000/NCG12813.html
http://localhost:8000/NCG12843.html
http://localhost:8000/NCG12849.html
http://localhost:8000/NCG12879.html
http://localhost:8000/NCG13029.html
http://localhost:8000/NCG13032.html
http://localhost:8000/NCG13038.html
http://localhost:8000/NCG13064.html
http://localhost:8000/NCG13146.html
http://localhost:8000/NCG13379.html
http://localhost:8000/NCG13387.html
http://localhost:8000/NCG13388.html
http://localhost:8000/NCG13564.html
http://localhost:8000/NCG13823.html
http://localhost:8000/NCG14054.html
http://localhost:8000/NCG14096.html
http://localhost:8000/NCG14228.html
http://localhost:8000/NCG14245.html
http://localhost:8000/NCG14294.html
http://localhost:8000/NCG14309.html
http://localhost:8000/NCG14509.html
http://localhost:8000/NCG14568.html
http://localhost:8000/NCG14636.html
http://localhost:8000/NCG14913.html
http://localhost:8000/NCG14951.html
http://localhost:8000/NCG15029.html
http://localhost:8000/NCG15057.html
http://localhost:8000/NCG15139.html
http://localhost:8000/NCG15184.html
http://localhost:8000/NCG15249.html
http://localhost:8000/NCG15283.html
http://localhost:8000/NCG15323.html
http://localhost:8000/NCG15327.html
http://localhost:8000/NCG15343.html
http://localhost:8000/NCG15470.html
http://localhost:8000/NCG15509.html
http://localhost:8000/NCG15547.html
http://localhost:8000/NCG15629.html
http://localhost:8000/NCG15636.html
http://localhost:8000/NCG15657.html
http://localhost:8000/NCG15726.html
http://localhost:8000/NCG15864.html
http://localhost:8000/NCG16040.html
http://localhost:8000/NCG16096.html
http://localhost:8000/NCG16175.html
http://localhost:8000/NCG16194.html
http://localhost:8000/NCG16221.html
http://localhost:8000/NCG16269.html
http://localhost:8000/NCG16270.html
http://localhost:8000/NCG16273.html
http://localhost:8000/NCG16295.html
http://localhost:8000/NCG16362.html
http://localhost:8000/NCG16545.html
http://localhost:8000/NCG16574.html
http://localhost:8000/NCG16718.html
