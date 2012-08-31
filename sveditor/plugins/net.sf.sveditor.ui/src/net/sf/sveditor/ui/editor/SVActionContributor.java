/****************************************************************************
 * Copyright (c) 2008-2010 Matthew Ballance and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Ballance - initial implementation
 ****************************************************************************/


package net.sf.sveditor.ui.editor;

import java.util.ResourceBundle;

import net.sf.sveditor.ui.SVUiPlugin;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;

public class SVActionContributor extends TextEditorActionContributor {
	
	protected RetargetTextEditorAction fContentAssistProposal;
	protected RetargetTextEditorAction fIndentAction;
	
	protected RetargetTextEditorAction fOpenDeclarationAction;
	protected RetargetTextEditorAction fOpenTypeAction;
	protected RetargetTextEditorAction fFindReferencesAction;
	protected RetargetTextEditorAction fOpenTypeHierarchyAction;
	protected RetargetTextEditorAction fOpenObjectsAction;
	protected RetargetTextEditorAction fOpenQuickObjectsAction;
	protected RetargetTextEditorAction fAddBlockCommentAction;
	protected RetargetTextEditorAction fRemoveBlockCommentAction;
	protected RetargetTextEditorAction fToggleCommentAction;
	protected RetargetTextEditorAction fNextWordAction;
	protected RetargetTextEditorAction fPrevWordAction;
	protected RetargetTextEditorAction fSelNextWordAction;
	protected RetargetTextEditorAction fSelPrevWordAction;
	
	protected MenuManager			   fSourceMenu;

	public SVActionContributor() {
		super();
		ResourceBundle bundle = SVUiPlugin.getDefault().getResources();
		
		fContentAssistProposal = new RetargetTextEditorAction(
				bundle, "ContentAssistProposal.");
		fContentAssistProposal.setActionDefinitionId(
				ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		
		fOpenDeclarationAction = new RetargetTextEditorAction(
				bundle, "OpenDeclaration.");
		fOpenDeclarationAction.setActionDefinitionId(
				"net.sf.sveditor.ui.editor.open.declaration");
		
		
		fFindReferencesAction = new RetargetTextEditorAction(
				bundle, "FindReferences.");
		fFindReferencesAction.setActionDefinitionId(
				"net.sf.sveditor.ui.editor.find.references");
		
		fOpenTypeAction = new RetargetTextEditorAction(
				bundle, "OpenType.");
		fOpenTypeAction.setActionDefinitionId(
				"net.sf.sveditor.ui.editor.open.type");

		fOpenTypeHierarchyAction = new RetargetTextEditorAction(
				bundle, "OpenTypeHierarchy.");
		fOpenTypeHierarchyAction.setActionDefinitionId(
				"net.sf.sveditor.ui.editor.open.type.hierarchy");

		fOpenObjectsAction = new RetargetTextEditorAction(
				bundle, "OpenObjects.");
		fOpenObjectsAction.setActionDefinitionId(
				"net.sf.sveditor.ui.editor.open.objects");

		fOpenQuickObjectsAction = new RetargetTextEditorAction(
				bundle, "OpenQuickObjects.");
		fOpenQuickObjectsAction.setActionDefinitionId(
				"net.sf.sveditor.ui.editor.open.quick.objects");

		fIndentAction = new RetargetTextEditorAction(bundle, "Indent.");
		fIndentAction.setActionDefinitionId("net.sf.sveditor.ui.indent");
		
		fAddBlockCommentAction = new RetargetTextEditorAction(bundle, "AddBlockComment.");
		fAddBlockCommentAction.setActionDefinitionId("net.sf.sveditor.ui.AddBlockComment");
		
		fRemoveBlockCommentAction = new RetargetTextEditorAction(bundle, "RemoveBlockComment.");
		fRemoveBlockCommentAction.setActionDefinitionId("net.sf.sveditor.ui.RemoveBlockComment");
		
		fToggleCommentAction = new RetargetTextEditorAction(bundle, "ToggleComment.");
		fToggleCommentAction.setActionDefinitionId(SVUiPlugin.PLUGIN_ID + ".ToggleComment");
		
		fNextWordAction = new RetargetTextEditorAction(bundle, "NextWordAction.");
		fNextWordAction.setActionDefinitionId(ITextEditorActionDefinitionIds.WORD_NEXT);
		
		fPrevWordAction = new RetargetTextEditorAction(bundle, "PrevWordAction.");
		fPrevWordAction.setActionDefinitionId(ITextEditorActionDefinitionIds.WORD_PREVIOUS);
		
		fSelNextWordAction = new RetargetTextEditorAction(bundle, "SelNextWordAction.");
		fSelNextWordAction.setActionDefinitionId(ITextEditorActionDefinitionIds.SELECT_WORD_NEXT);
		
		fSelPrevWordAction = new RetargetTextEditorAction(bundle, "SelPrevWordAction.");
		fSelPrevWordAction.setActionDefinitionId(ITextEditorActionDefinitionIds.SELECT_WORD_PREVIOUS);
		
	}

	public void contributeToMenu(IMenuManager mm) {
		IMenuManager editMenu = 
			mm.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
		if (editMenu != null) {
			editMenu.add(new Separator());
			editMenu.add(fContentAssistProposal);
			editMenu.add(fOpenDeclarationAction);
			editMenu.add(fOpenTypeHierarchyAction);
			editMenu.add(fOpenTypeAction);
			editMenu.add(fOpenObjectsAction);
			editMenu.add(fOpenQuickObjectsAction);
			editMenu.add(fFindReferencesAction);
			editMenu.add(fIndentAction);
		}
	}
	
	/*
	 * @see IEditorActionBarContributor#init(IActionBars)
	 */
	public void init(IActionBars bars) {
		super.init(bars);

		IMenuManager menuManager = bars.getMenuManager();
		IMenuManager editMenu = menuManager.findMenuUsingPath(
				IWorkbenchActionConstants.M_EDIT);
		
		if (editMenu != null) {
			editMenu.add(new Separator());
			editMenu.add(fContentAssistProposal);
			editMenu.add(fOpenDeclarationAction);
			editMenu.add(fOpenTypeAction);
			editMenu.add(fOpenTypeHierarchyAction);
			editMenu.add(fOpenQuickObjectsAction);
			editMenu.add(fOpenObjectsAction);
			editMenu.add(fFindReferencesAction);
			editMenu.add(fIndentAction);
		}	
	}
	
	private void doSetActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);

		ITextEditor editor= null;
		if (part instanceof ITextEditor)
			editor= (ITextEditor) part;

		fContentAssistProposal.setAction(getAction(editor, "ContentAssistProposal")); //$NON-NLS-1$
		fOpenDeclarationAction.setAction(getAction(editor, "OpenDeclaration"));
		fOpenTypeAction.setAction(getAction(editor, "OpenType"));
		fOpenTypeHierarchyAction.setAction(getAction(editor, "OpenTypeHierarchy"));
		fOpenObjectsAction.setAction(getAction(editor, "OpenObjects"));
		fOpenQuickObjectsAction.setAction(getAction(editor, "OpenQuickObjects"));
		fFindReferencesAction.setAction(getAction(editor, "FindReferences"));
		fIndentAction.setAction(getAction(editor, "Indent"));
		fAddBlockCommentAction.setAction(getAction(editor, "AddBlockComment"));
		fRemoveBlockCommentAction.setAction(getAction(editor, "RemoveBlockComment"));
		fToggleCommentAction.setAction(getAction(editor, "ToggleComment"));
		fNextWordAction.setAction(getAction(editor, "NextWord"));
		fPrevWordAction.setAction(getAction(editor, "PrevWord"));
		fSelNextWordAction.setAction(getAction(editor, "SelNextWord"));
		fSelPrevWordAction.setAction(getAction(editor, "SelPrevWord"));
	}

	/*
	 * @see IEditorActionBarContributor#setActiveEditor(IEditorPart)
	 */
	public void setActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);
		doSetActiveEditor(part);
	}

	/*
	 * @see IEditorActionBarContributor#dispose()
	 */
	public void dispose() {
		doSetActiveEditor(null);
		super.dispose();
	}

}
