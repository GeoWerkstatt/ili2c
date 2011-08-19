package ch.interlis.models.IlisMeta07.ModelData;
public class MultiValue extends ch.interlis.models.IlisMeta07.ModelData.TypeRelatedType
{
  private final static String tag= "IlisMeta07.ModelData.MultiValue";
  public MultiValue(String oid) {
    super(oid);
  }
  public String getobjecttag() {
    return tag;
  }
  public boolean getOrdered() {
    String value=getattrvalue("Ordered");
    if(value==null)throw new IllegalStateException();
    return value.equals("true");
  }
  public void setOrdered(boolean value) {
    setattrvalue("Ordered", value?"true":"false");
  }
  public ch.interlis.models.IlisMeta07.ModelData.Multiplicity getMultiplicity() {
    ch.interlis.models.IlisMeta07.ModelData.Multiplicity value=(ch.interlis.models.IlisMeta07.ModelData.Multiplicity)getattrobj("Multiplicity",0);
    if(value==null)throw new IllegalStateException();
    return value;
  }
  public void setMultiplicity(ch.interlis.models.IlisMeta07.ModelData.Multiplicity value) {
    if(getattrvaluecount("Multiplicity")>0){
      changeattrobj("Multiplicity",0, value);
    }else{
      addattrobj("Multiplicity", value);
    }
  }
}
