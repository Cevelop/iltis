package ch.hsr.ifs.iltis.testing.highlevel.helpers;

import org.eclipse.ui.PlatformUI;

import ch.hsr.ifs.iltis.core.core.exception.ILTISException;
import ch.hsr.ifs.iltis.core.core.functional.functions.ThrowingRunnable;


public abstract class UIThreadSyncRunnable implements Runnable {

   volatile private Exception e;

   public static void run(ThrowingRunnable<Exception> runnable) {
      new UIThreadSyncRunnable() {

         @Override
         protected void runSave() throws Exception {
            runnable.run();
         }
      }.runSyncOnUIThread();
   }

   protected abstract void runSave() throws Exception;

   @Override
   final public void run() {
      try {
         runSave();
      } catch (Exception e) {
         this.e = e;
      }
   }

   private void throwIfHasException() throws RuntimeException {
      if (e != null) ILTISException.wrap(e).rethrowUnchecked();
   }

   final public void runSyncOnUIThread() throws RuntimeException {
      PlatformUI.getWorkbench().getDisplay().syncExec(this);
      throwIfHasException();
   }
}
