#!/bin/sh
# download testdata from the net

cd ../resources

if [ ! -f testfiles ]
then
    mkdir testfiles
fi

cd testfiles

# large video
if [ ! -f Wikimania_2011_-_Opening_ceremony_greetings_by_Meir_Sheetrit.ogv ]
then
    wget https://upload.wikimedia.org/wikipedia/commons/d/db/Wikimania_2011_-_Opening_ceremony_greetings_by_Meir_Sheetrit.ogv
fi

# smaller video
if [ ! -f Walking_caterpillar.ogv ]
then
    wget https://upload.wikimedia.org/wikipedia/commons/8/86/Walking_caterpillar.ogv
fi

# images

if [ ! -f Berliner_Fernsehturm_November_2013.jpg ]
then
    wget https://upload.wikimedia.org/wikipedia/commons/b/b9/Berliner_Fernsehturm_November_2013.jpg
fi

if [ ! -f Dunvegan_Castle_in_the_mist01editcrop_2007-08-22.jpg ]
then
    wget https://upload.wikimedia.org/wikipedia/commons/3/3b/Dunvegan_Castle_in_the_mist01editcrop_2007-08-22.jpg
fi

if [ ! -f 4_Cilindros,_Múnich,_Alemania,_2013-02-11,_DD_07.JPG ]
then
    wget https://upload.wikimedia.org/wikipedia/commons/5/52/4_Cilindros%2C_M%C3%BAnich%2C_Alemania%2C_2013-02-11%2C_DD_07.JPG
fi

if [ ! -f Gypful.jpg ]
then
    wget https://upload.wikimedia.org/wikipedia/commons/d/dc/Gypful.jpg
fi

if [ ! -f Ajaccio_phare_citadelle.jpg ]
then
    wget https://upload.wikimedia.org/wikipedia/commons/0/0b/Ajaccio_phare_citadelle.jpg
fi

if [ ! -f Death_Valley_exit_SR190_view_Panamint_Butt_flash_flood_2013.jpg ]
then
    wget https://upload.wikimedia.org/wikipedia/commons/2/2e/Death_Valley_exit_SR190_view_Panamint_Butt_flash_flood_2013.jpg
fi

if [ ! -f Calle_en_centro_de_Maracaibo.jpg ]
then
    wget https://upload.wikimedia.org/wikipedia/commons/9/98/Calle_en_centro_de_Maracaibo.jpg
fi

if [ ! -f Spb_06-2012_Palace_Embankment_various_01.jpg ]
then
    wget https://upload.wikimedia.org/wikipedia/commons/a/af/Spb_06-2012_Palace_Embankment_various_01.jpg
fi