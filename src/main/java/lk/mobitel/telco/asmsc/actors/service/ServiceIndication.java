package lk.mobitel.telco.asmsc.actors.service;

import lk.mobitel.telco.asmsc.map.constant.Primitive;

/**
 * Generic interface for service indications for
 * {@link lk.mobitel.telco.asmsc.actors.ServingDialogue}.
 */
public interface ServiceIndication {
  Primitive primitive();

  short invokeId();
}
