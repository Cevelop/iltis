package ch.hsr.ifs.iltis.cpp.versionator.wizard

import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager
import org.eclipse.core.resources.IProject
import org.eclipse.jface.wizard.Wizard
import org.eclipse.jface.wizard.WizardPage
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Label
import ch.hsr.ifs.iltis.cpp.versionator.definition.CPPVersion
import org.eclipse.swt.widgets.Combo
import org.eclipse.swt.widgets.Dialog
import org.eclipse.swt.events.SelectionListener
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.events.ModifyListener
import ch.hsr.ifs.iltis.cpp.versionator.operation.ChangeCompilerDialectFlagOperation
import ch.hsr.ifs.iltis.cpp.versionator.operation.ChangeIndexFlagOperation

internal class StandardVersionSelectionPage(project: IProject) : WizardPage("Select Standard Version") {

	private val currentVersion = CPPVersion.getForProject(project)
	private val projectHasVersionSet = currentVersion != null

	private lateinit var standardSelector: Combo

	val selectedStandard get() = enumValues<CPPVersion>()[standardSelector.selectionIndex]

	init {
		title = "Select Standard Version"
		description = "Select the desired C++ Standard version"
	}

	override fun createControl(parent: Composite?) {
		control = Composite(parent, SWT.NONE).apply {
			layout = GridLayout().apply {
				numColumns = 2
			}

			Label(this, SWT.NONE).run {
				text = "Set standard version to:"
			}

			standardSelector = Combo(this, SWT.DROP_DOWN or SWT.READ_ONLY).apply {
				enumValues<CPPVersion>().map(CPPVersion::getVersionString).forEach(this::add)
			}

			standardSelector.addSelectionListener(object : SelectionAdapter() {
				override fun widgetSelected(event: SelectionEvent) {
					updateMessage()
				}
			})

			standardSelector.addModifyListener {
				updateMessage()
			}

			standardSelector.run {
				if (projectHasVersionSet) {
					select(indexOf(currentVersion.versionString))
				} else {
					select(itemCount - 1)
				}
			}
		}
		setPageComplete(false)
	}

	private fun updateMessage() {
		when {
			projectHasVersionSet && currentVersion == selectedStandard -> {
				setMessage("Please select a new C++ standard version", WARNING)
				setPageComplete(false)
			}
			else -> {
				setMessage("The C++ standard version will be set to ${selectedStandard.versionString}", INFORMATION)
				setPageComplete(true)
			}
		}

	}

}

class ChangeStandardVersionWizard(private val project: IProject) : Wizard() {

	override fun addPages() = addPage(StandardVersionSelectionPage(project))

	override fun performFinish(): Boolean {
		(pages.last() as? StandardVersionSelectionPage)?.run {
			ChangeCompilerDialectFlagOperation().perform(project, selectedStandard, true)
			ChangeIndexFlagOperation().perform(project, selectedStandard, true)
			CPPVersion.setForProject(project, selectedStandard)
		}
		return true
	}

}