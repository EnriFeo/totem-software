package org.enrico.totemsoftware.controllers;

import java.util.List;

import org.enrico.totemsoftware.model.NodeModel;
import org.enrico.totemsoftware.model.NodeModel.NodeType;
import org.enrico.totemsoftware.services.TreeWalker;
import org.enrico.totemsoftware.utils.OnlyComparator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/totem")
@AllArgsConstructor
public class TotemViewsController {

	private TreeWalker treeWalker;

	@GetMapping("/**")
	public String endpoint(HttpServletRequest request, Model model) {
		String path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
		String result = null;

		OnlyComparator<NodeModel, NodeType> comparator = new OnlyComparator<>(treeWalker.resolvePath(path, "totem"),
				NodeModel::getNodeType);
		if (comparator.isOnlyOne()) {
			List<NodeModel> originalList = comparator.getOriginalList();
			NodeType nodeType = originalList.isEmpty() ? NodeType.FOLDER : comparator.getOnlyValue();
			switch (nodeType) {
			case FILE:
				model.addAttribute("node", originalList.getFirst());
				result = "nodeTemplate";
				break;
			case FOLDER:
				model.addAttribute("nodes", originalList);
				result = "nodeListTemplate";
				break;
			}
		} else {
			throw new IllegalStateException();
		}

		return result;

	}

}
