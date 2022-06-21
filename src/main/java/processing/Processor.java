package processing;

import java.util.Optional;

import exceptions.InvalidCommandException;
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
	
	public static Processor getInstance() {
		if(instance == null)
			instance = new Processor();
		return instance;
	}
	
	private Processor() {
		super();
		this.mediator = Mediator.getInstance();
		this.productService = ProductServiceImpl.getInstance();
		this.groupService = GroupServiceImpl.getInstance();
	}
	
	public void addProcessingTask(Packet packet) {
		this.execService.execute(() -> {
			Message msg = packet.getBMsg();
			var optional = Commands.valueOf(msg.getCType());
			if(optional.isEmpty())
				throw new InvalidCommandException(); // or simply return
			Commands command = optional.get();
			switch(command) {
				case PRODUCT_GET_QUANTITY : {
						int res = this.productService.getProductQuantity(msg.getMessageJSON().getLong(JSONStrings.PRODUCT_ID));
						this.mediator.notifyPacketProcessed(packet, Optional.ofNullable(res));
					}
					break;
				case PRODUCT_TAKE_QUANTITY : {
						this.productService.takeProducts(
								msg.getMessageJSON().getLong(JSONStrings.PRODUCT_ID), 
								msg.getMessageJSON().getInt(JSONStrings.QUANTITY_TO_REMOVE));
						this.mediator.notifyPacketProcessed(packet, Optional.ofNullable(null));
					}
					break;
				case PRODUCT_ADD_QUANTITY : {
						this.productService.addProducts(
								msg.getMessageJSON().getLong(JSONStrings.PRODUCT_ID), 
								msg.getMessageJSON().getInt(JSONStrings.QUANTITY_TO_ADD));
						this.mediator.notifyPacketProcessed(packet, Optional.ofNullable(null));
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
						this.mediator.notifyPacketProcessed(packet, Optional.ofNullable(null));
					}
					break;
			case GROUP_CREATE : {
					this.groupService.createGroup(
							msg.getMessageJSON().getString(JSONStrings.NAME), 
							msg.getMessageJSON().getString(JSONStrings.DESCRIPTION));
					this.mediator.notifyPacketProcessed(packet, Optional.ofNullable(null));
				}
				break;
			case PRODUCT_SET_PRICE : {
					this.productService.setProductPrice(
							msg.getMessageJSON().getLong(JSONStrings.PRODUCT_ID),
							msg.getMessageJSON().getDouble(JSONStrings.PRICE));
					this.mediator.notifyPacketProcessed(packet, Optional.ofNullable(null));
				}
			}
		});
	}
}
