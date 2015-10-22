package edu.tamu.framework.mapping;

import org.springframework.messaging.Message;
import org.springframework.messaging.handler.MessageCondition;
import org.springframework.messaging.simp.SimpMessageTypeMessageCondition;

public class CustomSimpMessageMappingInfo implements MessageCondition<CustomSimpMessageMappingInfo> {

	private final SimpMessageTypeMessageCondition messageTypeMessageCondition;

	private final WebSocketRequestCondition destinationConditions;


	public CustomSimpMessageMappingInfo(SimpMessageTypeMessageCondition messageTypeMessageCondition, WebSocketRequestCondition destinationConditions) {

		this.messageTypeMessageCondition = messageTypeMessageCondition;
		this.destinationConditions = destinationConditions;
	}


	public SimpMessageTypeMessageCondition getMessageTypeMessageCondition() {
		return this.messageTypeMessageCondition;
	}

	public WebSocketRequestCondition getDestinationConditions() {
		return this.destinationConditions;
	}


	@Override
	public CustomSimpMessageMappingInfo combine(CustomSimpMessageMappingInfo other) {
		SimpMessageTypeMessageCondition typeCond = this.getMessageTypeMessageCondition().combine(other.getMessageTypeMessageCondition());
		WebSocketRequestCondition destCond = this.destinationConditions.combine(other.getDestinationConditions());
		return new CustomSimpMessageMappingInfo(typeCond, destCond);
	}

	@Override
	public CustomSimpMessageMappingInfo getMatchingCondition(Message<?> message) {
		SimpMessageTypeMessageCondition typeCond = this.messageTypeMessageCondition.getMatchingCondition(message);
		if (typeCond == null) {
			return null;
		}
		WebSocketRequestCondition destCond = this.destinationConditions.getMatchingCondition(message);
		if (destCond == null) {
			return null;
		}
		return new CustomSimpMessageMappingInfo(typeCond, destCond);
	}

	@Override
	public int compareTo(CustomSimpMessageMappingInfo other, Message<?> message) {
		int result = this.messageTypeMessageCondition.compareTo(other.messageTypeMessageCondition, message);
		if (result != 0) {
			return result;
		}
		result = this.destinationConditions.compareTo(other.destinationConditions, message);
		if (result != 0) {
			return result;
		}
		return 0;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && obj instanceof CustomSimpMessageMappingInfo) {
			CustomSimpMessageMappingInfo other = (CustomSimpMessageMappingInfo) obj;
			return (this.destinationConditions.equals(other.destinationConditions) &&
					this.messageTypeMessageCondition.equals(other.messageTypeMessageCondition));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (this.destinationConditions.hashCode() * 31 + this.messageTypeMessageCondition.hashCode());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("{");
		builder.append(this.destinationConditions);
		builder.append(",messageType=").append(this.messageTypeMessageCondition);
		builder.append('}');
		return builder.toString();
	}

}