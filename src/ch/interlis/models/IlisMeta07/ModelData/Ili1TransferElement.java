package ch.interlis.models.IlisMeta07.ModelData;
public class Ili1TransferElement extends ch.interlis.iom_j.Iom_jObject
{
  private final static String tag= "IlisMeta07.ModelData.Ili1TransferElement";
  public Ili1TransferElement(String oid) {
    super(tag,oid);
  }
  public String getobjecttag() {
    return tag;
  }
  public String getIli1TransferClass() {
    ch.interlis.iom.IomObject value=getattrobj("Ili1TransferClass",0);
    if(value==null)throw new IllegalStateException();
    String oid=value.getobjectrefoid();
    if(oid==null)throw new IllegalStateException();
    return oid;
  }
  public void setIli1TransferClass(String oid) {
    ch.interlis.iom.IomObject structvalue=addattrobj("Ili1TransferClass","REF");
    structvalue.setobjectrefoid(oid);
  }
  public String getIli1RefAttr() {
    ch.interlis.iom.IomObject value=getattrobj("Ili1RefAttr",0);
    if(value==null)throw new IllegalStateException();
    String oid=value.getobjectrefoid();
    if(oid==null)throw new IllegalStateException();
    return oid;
  }
  public void setIli1RefAttr(String oid,long orderPos) {
    ch.interlis.iom.IomObject structvalue=addattrobj("Ili1RefAttr","REF");
    structvalue.setobjectrefoid(oid);
    structvalue.setobjectreforderpos(orderPos);
  }
}
