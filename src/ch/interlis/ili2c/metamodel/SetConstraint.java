 
package ch.interlis.ili2c.metamodel;

import java.util.*;

{
  public SetConstraint()
  {
  }
  private Evaluable preCondition=null;
  public Evaluable getPreCondition() {
  	return preCondition;
  }
  public void setPreCondition(Evaluable preCondition) {
  	this.preCondition = preCondition;
  }
}