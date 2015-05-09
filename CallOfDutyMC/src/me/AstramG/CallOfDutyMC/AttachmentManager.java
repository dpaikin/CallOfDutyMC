package me.AstramG.CallOfDutyMC;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

public class AttachmentManager {

		static List<Attachment> attachments = new ArrayList<Attachment>();
		
		CallOfDuty cod;
		
		public AttachmentManager(CallOfDuty cod) {
			this.cod = cod;
		}

		//AttachmentData: NAME:ID:PRICE:ITEM
		
		public void addAttachment(String attachmentName, String attachmentData) {
			String[] parsedData = attachmentData.split(":");
			Attachment attachment = new Attachment();
			attachment.attachmentName = parsedData[0];
			attachment.attachmentId = Integer.parseInt(parsedData[1]);
			attachment.price = Integer.parseInt(parsedData[2]);
			attachment.item = Material.valueOf(parsedData[3]);
			attachments.add(attachment);
		}
		
		public static Attachment getAttachment(String name) {
			for (Attachment attachment : attachments) {
				if (attachment.attachmentName.equalsIgnoreCase(name)) {
					return attachment;
				}
			}
			return null;
		}
		
	}
