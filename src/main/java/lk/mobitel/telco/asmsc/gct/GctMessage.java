package lk.mobitel.telco.asmsc.gct;

import lk.mobitel.telco.asmsc.map.Message;
import lk.mobitel.telco.asmsc.map.constant.MessageType;
import lk.mobitel.telco.asmsc.map.constant.ParameterName;
import lk.mobitel.telco.asmsc.map.constant.Primitive;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class GctMessage implements Message {

  public static class GctMessageBuilder {
    private MessageType type = MessageType.MAP_MSG_NULL;
    private Primitive primitive = Primitive.MAP_NULL_PRIMITIVE;
    private int id = 0;
    private int source = GctUtil.config().localModuleId();
    private int destination = GctUtil.config().remoteModuleId();
    private long instance = 0L;
    private boolean rspReq = false;
    private ArrayList<ParameterName> parameters_Keys;
    private ArrayList<GctParameter> parameters_Values;

    GctMessageBuilder() {}

    public GctMessageBuilder type(MessageType type) {
      this.type = type;
      return this;
    }

    public GctMessageBuilder primitive(Primitive primitive) {
      this.primitive = primitive;
      return this;
    }

    public GctMessageBuilder id(int id) {
      this.id = id;
      return this;
    }

    public GctMessageBuilder source(int source) {
      this.source = source;
      return this;
    }

    public GctMessageBuilder destination(int destination) {
      this.destination = destination;
      return this;
    }

    public GctMessageBuilder instance(long instance) {
      this.instance = instance;
      return this;
    }

    public GctMessageBuilder responseRequired() {
      return responseRequired(true);
    }

    public GctMessageBuilder responseNotRequired() {
      return responseRequired(false);
    }

    public GctMessageBuilder responseRequired(boolean rspReq) {
      this.rspReq = rspReq;
      return this;
    }

    public GctMessageBuilder parameter(GctParameter parameter) {
      if (this.parameters_Keys == null) {
        this.parameters_Keys = new ArrayList<>();
        this.parameters_Values = new ArrayList<>();
      }
      this.parameters_Keys.add(parameter.getName());
      this.parameters_Values.add(parameter);
      return this;
    }

    public GctMessageBuilder parameters(
        Map<? extends ParameterName, ? extends GctParameter> parameters) {
      if (this.parameters_Keys == null) {
        this.parameters_Keys = new ArrayList<>();
        this.parameters_Values = new ArrayList<>();
      }
      for (final Map.Entry<? extends ParameterName, ? extends GctParameter> entry :
          parameters.entrySet()) {
        this.parameters_Keys.add(entry.getKey());
        this.parameters_Values.add(entry.getValue());
      }
      return this;
    }

    public GctMessageBuilder parameters(List<? extends GctParameter> parameters) {
      if (this.parameters_Keys == null) {
        this.parameters_Keys = new ArrayList<>();
        this.parameters_Values = new ArrayList<>();
      }

      for (GctParameter parameter : parameters) {
        this.parameters_Keys.add(parameter.getName());
        this.parameters_Values.add(parameter);
      }
      return this;
    }

    public GctMessageBuilder clearParameters() {
      if (this.parameters_Keys != null) {
        this.parameters_Keys.clear();
        this.parameters_Values.clear();
      }
      return this;
    }

    public GctMessage build() {
      Map<ParameterName, GctParameter> parameters;
      switch (this.parameters_Keys == null ? 0 : this.parameters_Keys.size()) {
        case 0:
          parameters = java.util.Collections.emptyMap();
          break;
        case 1:
          parameters =
              java.util.Collections.singletonMap(
                  this.parameters_Keys.get(0), this.parameters_Values.get(0));
          break;
        default:
          parameters =
              new java.util.LinkedHashMap<>(
                  this.parameters_Keys.size() < 1073741824
                      ? 1 + this.parameters_Keys.size() + (this.parameters_Keys.size() - 3) / 3
                      : Integer.MAX_VALUE);
          for (int i = 0; i < this.parameters_Keys.size(); i++)
            parameters.put(this.parameters_Keys.get(i), this.parameters_Values.get(i));
          parameters = java.util.Collections.unmodifiableMap(parameters);
      }

      return new GctMessage(type, primitive, id, source, destination, instance, rspReq, parameters);
    }

    public String toString() {
      return "GctMessage.GctMessageBuilder(type="
          + this.type
          + ", primitive="
          + this.primitive
          + ", id="
          + this.id
          + ", source="
          + this.source
          + ", destination="
          + this.destination
          + ", origin="
          + this.instance
          + ", responseRequired="
          + this.rspReq
          + ", parameters/key="
          + this.parameters_Keys
          + ", parameters/value="
          + this.parameters_Values
          + ")";
    }
  }

  @Contract(" -> new")
  public static @NotNull GctMessageBuilder builder() {
    return new GctMessageBuilder();
  }

  private final MessageType type;
  private final Primitive primitive;
  private final int id;
  private final int source;
  private final int destination;
  private final long instance;
  private final boolean responseRequired;
  private final Map<ParameterName, GctParameter> parameters;

  private GctMessage(
      MessageType type,
      Primitive primitive,
      int id,
      int source,
      int destination,
      long instance,
      boolean responseRequired,
      Map<ParameterName, GctParameter> parameters) {
    this.type = type;
    this.primitive = primitive;
    this.id = id;
    this.source = source;
    this.destination = destination;
    this.instance = instance;
    this.responseRequired = responseRequired;
    this.parameters = parameters;
  }

  @Override
  public MessageType getType() {
    return type;
  }

  @Override
  public Primitive getPrimitive() {
    return primitive;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public int getSource() {
    return source;
  }

  @Override
  public int getDestination() {
    return destination;
  }

  @Override
  public long getInstance() {
    return instance;
  }

  @Override
  public boolean isResponseRequired() {
    return responseRequired;
  }

  @Override
  public GctParameter getParameter(ParameterName name) throws NoSuchElementException {
    GctParameter param = parameters.get(name);
    if (param == null)
      throw new NoSuchElementException(
          "No such parameter found in this message(#" + id + "): " + name);
    return param;
  }

  @Override
  public List<GctParameter> parameterList() {
    return List.copyOf(parameters.values());
  }

  @Override
  public boolean hasParameter(ParameterName name) {
    return parameters.containsKey(name);
  }

  @Override
  public int parameterCount() {
    return parameters.size();
  }

  @Override
  public String toString() {
    StringBuilder paramStr = new StringBuilder("[ ");
    for (GctParameter param : parameters.values()) {
      paramStr.append(param.toString()).append(", ");
    }
    paramStr.replace(paramStr.length() - 2, paramStr.length(), " ]");

    return String.format(
        "{ \"type\": \"%s\", \"primitive\": \"%s\", "
            + "\"id\": %d, \"source\": %d, \"destination\": %d, \"origin\": %d, "
            + "\"response_required\": %b, \"parameters\": %s }",
        type.mnemonic(),
        primitive.mnemonic(),
        id,
        source,
        destination,
        instance,
        responseRequired,
        paramStr);
  }
}
