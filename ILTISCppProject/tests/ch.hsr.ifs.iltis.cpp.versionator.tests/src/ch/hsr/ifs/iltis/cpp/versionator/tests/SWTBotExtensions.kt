package ch.hsr.ifs.iltis.cpp.versionator.tests

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException

fun SWTBotTable.getTableItemById(id: String): SWTBotTableItem {
	for (row in 0 until rowCount()) {
		if (getTableItem(row).id == id) {
			return getTableItem(row)
		}
	}
	throw WidgetNotFoundException("No table item with id '$id' was found!")
}

fun SWTWorkbenchBot.settle() {
	sleep(2000)
}