
package org.recap.ils.model.nypl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.camel.util.json.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "43",
        "44",
        "45",
        "46",
        "47",
        "48",
        "49",
        "50",
        "51",
        "53",
        "54",
        "55",
        "56",
        "80",
        "81",
        "83",
        "84",
        "85",
        "86",
        "95",
        "96",
        "98",
        "99",
        "102",
        "103",
        "104",
        "105",
        "122",
        "123",
        "124",
        "125",
        "126",
        "158",
        "163",
        "263",
        "268",
        "269",
        "270",
        "271",
        "297"
})
public class FixedFieldsUT {

    @JsonProperty("43")
    private org.recap.ils.model.nypl.patron._43 _43;
    @JsonProperty("44")
    private org.recap.ils.model.nypl.patron._44 _44;
    @JsonProperty("45")
    private org.recap.ils.model.nypl.patron._45 _45;
    @JsonProperty("46")
    private org.recap.ils.model.nypl.patron._46 _46;
    @JsonProperty("47")
    private org.recap.ils.model.nypl.patron._47 _47;
    @JsonProperty("48")
    private org.recap.ils.model.nypl.patron._48 _48;
    @JsonProperty("49")
    private org.recap.ils.model.nypl.patron._49 _49;
    @JsonProperty("50")
    private org.recap.ils.model.nypl.patron._50 _50;
    @JsonProperty("51")
    private org.recap.ils.model.nypl.patron._51 _51;
    @JsonProperty("53")
    private org.recap.ils.model.nypl.patron._53 _53;
    @JsonProperty("54")
    private org.recap.ils.model.nypl.patron._54 _54;
    @JsonProperty("55")
    private org.recap.ils.model.nypl.patron._55 _55;
    @JsonProperty("56")
    private org.recap.ils.model.nypl.patron._56 _56;
    @JsonProperty("80")
    private org.recap.ils.model.nypl.patron._80 _80;
    @JsonProperty("81")
    private org.recap.ils.model.nypl.patron._81 _81;
    @JsonProperty("83")
    private org.recap.ils.model.nypl.patron._83 _83;
    @JsonProperty("84")
    private org.recap.ils.model.nypl.patron._84 _84;
    @JsonProperty("85")
    private org.recap.ils.model.nypl.patron._85 _85;
    @JsonProperty("86")
    private org.recap.ils.model.nypl.patron._86 _86;
    @JsonProperty("95")
    private org.recap.ils.model.nypl.patron._95 _95;
    @JsonProperty("96")
    private org.recap.ils.model.nypl.patron._96 _96;
    @JsonProperty("98")
    private org.recap.ils.model.nypl.patron._98 _98;
    @JsonProperty("99")
    private org.recap.ils.model.nypl.patron._99 _99;
    @JsonProperty("102")
    private org.recap.ils.model.nypl.patron._102 _102;
    @JsonProperty("103")
    private org.recap.ils.model.nypl.patron._103 _103;
    @JsonProperty("104")
    private org.recap.ils.model.nypl.patron._104 _104;
    @JsonProperty("105")
    private org.recap.ils.model.nypl.patron._105 _105;
    @JsonProperty("122")
    private org.recap.ils.model.nypl.patron._122 _122;
    @JsonProperty("123")
    private org.recap.ils.model.nypl.patron._123 _123;
    @JsonProperty("124")
    private org.recap.ils.model.nypl.patron._124 _124;
    @JsonProperty("125")
    private org.recap.ils.model.nypl.patron._125 _125;
    @JsonProperty("126")
    private org.recap.ils.model.nypl.patron._126 _126;
    @JsonProperty("158")
    private org.recap.ils.model.nypl.patron._158 _158;
    @JsonProperty("163")
    private org.recap.ils.model.nypl.patron._163 _163;
    @JsonProperty("263")
    private org.recap.ils.model.nypl.patron._263 _263;
    @JsonProperty("268")
    private org.recap.ils.model.nypl.patron._268 _268;
    @JsonProperty("269")
    private org.recap.ils.model.nypl.patron._269 _269;
    @JsonProperty("270")
    private org.recap.ils.model.nypl.patron._270 _270;
    @JsonProperty("271")
    private org.recap.ils.model.nypl.patron._271 _271;
    @JsonProperty("297")
    private org.recap.ils.model.nypl.patron._297 _297;
    @Test
    public void getFixedFields(){
        FixedFields fixedFields = new FixedFields();
        fixedFields.set43(_43);
        fixedFields.get43();
        fixedFields.set44(_44);
        fixedFields.get44();
        fixedFields.set45(_45);
        fixedFields.get45();
        fixedFields.set46(_46);
        fixedFields.get46();
        fixedFields.set47(_47);
        fixedFields.get47();
        fixedFields.set48(_48);
        fixedFields.get48();
        fixedFields.set49(_49);
        fixedFields.get49();
        fixedFields.set50(_50);
        fixedFields.get50();
        fixedFields.set51(_51);
        fixedFields.get51();
        fixedFields.set53(_53);
        fixedFields.get53();
        fixedFields.set54(_54);
        fixedFields.get54();
        fixedFields.set55(_55);
        fixedFields.get55();
        fixedFields.set56(_56);
        fixedFields.get56();
        fixedFields.set80(_80);
        fixedFields.get80();
        fixedFields.set81(_81);
        fixedFields.get81();
        fixedFields.set83(_83);
        fixedFields.get83();
        fixedFields.set84(_84);
        fixedFields.get84();
        fixedFields.set85(_85);
        fixedFields.get85();
        fixedFields.set86(_86);
        fixedFields.get86();
        fixedFields.set95(_95);
        fixedFields.get95();
        fixedFields.set96(_96);
        fixedFields.get96();
        fixedFields.set98(_98);
        fixedFields.get98();
        fixedFields.set99(_99);
        fixedFields.get99();
        fixedFields.set102(_102);
        fixedFields.get102();
        fixedFields.set103(_103);
        fixedFields.get103();
        fixedFields.set104(_104);
        fixedFields.get104();
        fixedFields.set105(_105);
        fixedFields.get105();
        fixedFields.set122(_122);
        fixedFields.get122();
        fixedFields.set123(_123);
        fixedFields.get123();
        fixedFields.set124(_124);
        fixedFields.get124();
        fixedFields.set125(_125);
        fixedFields.get125();
        fixedFields.set126(_126);
        fixedFields.get126();
        fixedFields.set158(_158);
        fixedFields.get158();
        fixedFields.set163(_163);
        fixedFields.get163();
        fixedFields.set263(_263);
        fixedFields.get263();
        fixedFields.set268(_268);
        fixedFields.get268();
        fixedFields.set269(_269);
        fixedFields.get269();
        fixedFields.set270(_270);
        fixedFields.get270();
        fixedFields.set271(_271);
        fixedFields.get271();
        fixedFields.set297(_297);
        fixedFields.get297();
    }



}
