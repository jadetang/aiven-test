package github.jadetang.aiven.metric.producer.collector;

public interface MachineIdentifyProvider {

  /**
   * Return a unique identify for the current machine. It depends on the current infrastructure. For example, in AWS, an
   * instance ID would be a good machine identify. https://aws.amazon.com/premiumsupport/knowledge-center/batch-instance-id-ip-address/
   *
   * @return a unique identify for the current machine.
   */
  String machineIdentify();
}
