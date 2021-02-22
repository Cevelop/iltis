package ch.hsr.ifs.iltis.cpp.versionator.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.core.resources.IProject
import org.eclipse.cdt.core.model.ICProject
import org.eclipse.cdt.core.model.CoreModel
import org.eclipse.jface.dialogs.MessageDialog
import ch.hsr.ifs.iltis.cpp.versionator.wizard.ChangeStandardVersionWizard
import org.eclipse.jface.wizard.WizardDialog


class ChangeStandardVersionHandler : AbstractHandler() {

	override fun execute(event: ExecutionEvent): Any? =
		HandlerUtil.getCurrentSelection(event)?.run {
			if (this is IStructuredSelection) {
				handle(event, this)
			}
			null
		}

	private fun handle(event: ExecutionEvent, selection: IStructuredSelection) =
		selection.toList()
			.filterIsInstance(IProject::class.java)
			.forEach{ handle(event, it) }

	private fun handle(event: ExecutionEvent, project: IProject) {
		val shell = HandlerUtil.getActiveWorkbenchWindow(event).shell
		with(WizardDialog(shell, ChangeStandardVersionWizard(project))) {
			this.open()
		}
	}
}
