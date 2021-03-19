package io.turntabl.producer.client;

import io.turntabl.producer.clientOrders.OrderRequest;
import io.turntabl.producer.clientOrders.OrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

@Service
public class SoapClient {

  @Autowired
  Environment env;

  @Autowired
  private Jaxb2Marshaller marshaller;

  private WebServiceTemplate template;

  public OrderResponse getOrderStatus(OrderRequest request) {
    template = new WebServiceTemplate(marshaller);
    OrderResponse response = (OrderResponse) template.marshalSendAndReceive(env.getProperty("app.OVS_SOAP_API"),
        request);
    return response;
  }

}
