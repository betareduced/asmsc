package lk.mobitel.telco.asmsc.actors.service;

import lk.mobitel.telco.asmsc.map.Parameter;
import lk.mobitel.telco.asmsc.map.constant.Primitive;
import lombok.Value;

@Value
public class SendRtgInfoIndication implements ServiceIndication {
  Primitive primitive;
  short invokeId;
  Parameter msisdn;
  Parameter scAddr;
}
