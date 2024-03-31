package org.enrico.totemsoftware.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class NodeModel {

	public enum NodeType {
		FILE, FOLDER
	}

	private String name;
	private NodeType nodeType;

	private String link;
	private String mediaType;

	private String previous;
	private String next;
}
