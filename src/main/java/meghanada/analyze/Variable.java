package meghanada.analyze;

import com.google.common.base.MoreObjects;
import java.io.Serializable;
import meghanada.reflect.CandidateUnit;
import meghanada.reflect.FieldDescriptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Variable implements Serializable {

  private static final long serialVersionUID = 5911384112219223687L;
  private static Logger log = LogManager.getLogger(Variable.class);

  public String name;
  public int pos;
  public Range range;
  public String fqcn;

  public boolean isDef;
  public boolean isParameter;
  public boolean isField;
  public int argumentIndex = -1;

  public Variable(final String name, final int pos, final Range range) {
    this.name = name;
    this.pos = pos;
    this.range = range;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("name", name)
        .add("fqcn", fqcn)
        .add("range", range)
        .add("isField", isField)
        .add("isDef", isDef)
        .add("isParameter", isParameter)
        .add("pos", pos)
        .toString();
  }

  public boolean isDecl() {
    return isDef;
  }

  public CandidateUnit toCandidateUnit() {
    return FieldDescriptor.createVar("", this.name, this.fqcn);
  }
}
