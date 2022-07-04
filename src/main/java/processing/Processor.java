package processing;

import java.io.OutputStream;
import java.util.Optional;

import message.Message;
import packet.Packet;
import service.group.GroupService;
import service.group.GroupServiceImpl;
import service.product.ProductService;
import service.product.ProductServiceImpl;
import utils.Commands;
import utils.JSONStrings;

public class Processor extends BaseMultiThreadUnit {

	private static Processor instance;
	
	private Mediator mediator;
	
	private ProductService productService;
	
	private GroupService groupService;
	
	public static Processor getInstance(Mediator mediator) {
		if(instance == null)
			instance = new Processor(mediator);
		return instance;
	}
	
	private Processor(Mediator mediator) {
		super();
		this.mediator = mediator;
		this.productService = ProductServiceImpl.getInstance();
		this.groupService = GroupServiceImpl.getInstance();
	}
	
	public void addProcessingTask(Packet packet, OutputStream outStream) {
		this.execService.execute(() -> {
			Message msg = packet.getBMsg();
			var optional = Commands.valueOf(msg.getCType());
			if(optional.isEmpty()) {
				if(outStream == null)
					this.mediator.notifyPacketProcessed(packet, false, Optional.ofNullable(null), Optional.ofNullable("Such command doesn`t exist"));
				else
					this.mediator.notifyPacketProcessed(packet, false, Optional.ofNullable(null), Optional.ofNullable("Such command doesn`t exist"), outStream);
				return;
			}
			Commands command = optional.get();
			Optional<Object> possibleResult = Optional.ofNullable(null);
			try {
				switch(command) {
					case PRODUCT_GET_QUANTITY : {
							int res = this.productService.getProductQuantity(msg.getMessageJSON().getLong(JSONStrings.PRODUCT_ID));
							possibleResult = Optional.ofNullable(res);
						}
						break;
					case PRODUCT_TAKE_QUANTITY : {
							this.productService.takeProducts(
									msg.getMessageJSON().getLong(JSONStrings.PRODUCT_ID), 
									msg.getMessageJSON().getInt(JSONStrings.QUANTITY_TO_REMOVE));
						}
						break;
					case PRODUCT_ADD_QUANTITY : {
							this.productService.addProducts(
									msg.getMessageJSON().getLong(JSONStrings.PRODUCT_ID), 
									msg.getMessageJSON().getInt(JSONStrings.QUANTITY_TO_ADD));
						}
						break;
					case PRODUCT_CREATE : {
							var json = msg.getMessageJSON();
							this.productService.createProduct(
									json.getString(JSONStrings.NAME), 
									json.getString(JSONStrings.DESCRIPTION), 
									json.getString(JSONStrings.PRODUCER), 
									json.getInt(JSONStrings.QUANTITY), 
									json.getDouble(JSONStrings.PRICE), 
									json.getLong(JSONStrings.GROUP_ID));
						}
						break;
				case GROUP_CREATE : {
						this.groupService.createGroup(
								msg.getMessageJSON().getString(JSONStrings.NAME), 
								msg.getMessageJSON().getString(JSONStrings.DESCRIPTION));
					}
					break;
				case PRODUCT_SET_PRICE : {
						this.productService.setProductPrice(
								msg.getMessageJSON().getLong(JSONStrings.PRODUCT_ID),
								msg.getMessageJSON().getDouble(JSONStrings.PRICE));
					}
				}
				if(outStream == null)
					this.mediator.notifyPacketProcessed(packet, true, possibleResult, Optional.ofNullable(null));
				else
					this.mediator.notifyPacketProcessed(packet, true, possibleResult, Optional.ofNullable(null), outStream);
			} catch(RuntimeException e) {
				if(outStream == null)
					this.mediator.notifyPacketProcessed(packet, false, Optional.ofNullable(null), Optional.ofNullable(e.getMessage()));
				else
					this.mediator.notifyPacketProcessed(packet, false, Optional.ofNullable(null), Optional.ofNullable(e.getMessage()), outStream);
			}
		});
	}
}
