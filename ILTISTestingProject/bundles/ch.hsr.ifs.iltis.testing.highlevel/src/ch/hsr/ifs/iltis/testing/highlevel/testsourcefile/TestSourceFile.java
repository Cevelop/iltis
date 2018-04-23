/*******************************************************************************
 * Copyright (c) 2008 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 *******************************************************************************/
package ch.hsr.ifs.iltis.testing.highlevel.testsourcefile;

import java.util.Optional;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;


/**
 * The representation of a test-source file
 * 
 * @author Tobias Stauber, Emanuel Graf, Lukas Felber
 *
 */
public class TestSourceFile {

   private static final String NL      = System.getProperty("line.separator");
   private static final int    NOT_SET = -1;

   private final String             name;
   private final StringBuilder      source = new StringBuilder();
   private StringBuilder            expectedSource;
   private Optional<ITextSelection> selection;

   private int selectionStart = NOT_SET;
   private int selectionEnd   = NOT_SET;

   /**
    * Create a new test-source file
    * 
    * @param name
    *        The name of the test-source file
    */
   public TestSourceFile(final String name) {
      this.name = name;
   }

   public String getExpectedSource() {
      if (expectedSource != null) {
         return expectedSource.toString();
      } else {
         return getSource();
      }
   }

   public String getName() {
      return name;
   }

   @Override
   public String toString() {
      return getName();
   }

   public String getSource() {
      return source.toString();
   }

   int getSourceLengthOnWhichToAppend() {
      return source.length() != 0 ? source.length() + NL.length() : getSource().length();
   }

   void appendLineToSource(final String line) {
      if (source.length() != 0) source.append(NL);
      source.append(line);
   }

   void appendLineToExpectedSource(final String line) {
      if (expectedSource.length() != 0) expectedSource.append(NL);
      expectedSource.append(line);
   }

   void setSelectionStart(int start) {
      selectionStart = start;
   }

   void setSelectionEnd(int end) {
      selectionEnd = end;
   }

   void setSelectionStartRelativeToNextLine(int start) {
      selectionStart = start + getSourceLengthOnWhichToAppend();
   }

   void setSelectionEndRelativeToNextLine(int end) {
      selectionEnd = end + getSourceLengthOnWhichToAppend();
   }

   public boolean hasSelection() {
      return selectionStart != NOT_SET;
   }

   public Optional<ITextSelection> getSelection() {
      if (selection == null) {
         if (selectionStart >= 0 && selectionEnd < 0) {
            /* No selection end tag encountered -> assume end of source */
            selection = Optional.of(new TextSelection(selectionStart, source.length() - selectionStart));
         } else if (selectionStart < 0 || selectionEnd < 0) {
            selection = Optional.empty();
         } else {
            selection = Optional.of(new TextSelection(selectionStart, selectionEnd - selectionStart));
         }
      }
      return selection;
   }

   public void initExpectedSource() {
      expectedSource = new StringBuilder();
   }
}
