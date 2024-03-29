package org.jbpm.gd.pf.editor;

import org.eclipse.ui.part.FileEditorInput;
import org.jbpm.gd.common.editor.ContentProvider;
import org.jbpm.gd.common.editor.Editor;
import org.jbpm.gd.common.editor.GraphicalViewer;
import org.jbpm.gd.common.editor.OutlineViewer;
import org.jbpm.gd.common.model.SemanticElement;
import org.jbpm.gd.pf.part.PageFlowEditorOutlineEditPartFactory;
import org.jbpm.gd.pf.part.PageFlowGraphicalEditPartFactory;

public class PageFlowEditor extends Editor {

	public String getDefaultImageFileName() {
		String str = ((FileEditorInput)getEditorInput()).getName();
		int index = str.indexOf(".xml");
		str = index > -1 ? str.substring(0, index) : str;
		return str + ".jpg";
	}

	protected ContentProvider createContentProvider() {
		return new PageFlowContentProvider();
	}

	protected GraphicalViewer createGraphicalViewer() {
		return new GraphicalViewer(this) {
			protected void initEditPartFactory() {
				setEditPartFactory(new PageFlowGraphicalEditPartFactory());
			}			
		};
	}

	protected SemanticElement createMainElement() {
		return getSemanticElementFactory().createById("org.jbpm.gd.pf.pageFlowDefinition");
	}

	protected OutlineViewer createOutlineViewer() {
		return new OutlineViewer(this) {
			protected void initEditPartFactory() {
				setEditPartFactory(new PageFlowEditorOutlineEditPartFactory());
			}
		};
	}

}
