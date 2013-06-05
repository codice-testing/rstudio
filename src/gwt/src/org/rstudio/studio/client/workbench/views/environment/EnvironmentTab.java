/*
 * EnvironmentTab.java
 *
 * Copyright (C) 2009-12 by RStudio, Inc.
 *
 * Unless you have received this program directly from RStudio pursuant
 * to the terms of a commercial license agreement with RStudio, then
 * this program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http://www.gnu.org/licenses/agpl-3.0.txt) for more details.
 *
 */

package org.rstudio.studio.client.workbench.views.environment;

import org.rstudio.core.client.command.CommandBinder;
import org.rstudio.core.client.command.Handler;
import org.rstudio.studio.client.application.events.EventBus;
import org.rstudio.studio.client.common.filetypes.events.OpenDataFileHandler;
import org.rstudio.studio.client.workbench.commands.Commands;
import org.rstudio.studio.client.workbench.events.SessionInitEvent;
import org.rstudio.studio.client.workbench.events.SessionInitHandler;
import org.rstudio.studio.client.workbench.model.Session;
import org.rstudio.studio.client.workbench.ui.DelayLoadTabShim;
import org.rstudio.studio.client.workbench.ui.DelayLoadWorkbenchTab;
import org.rstudio.studio.client.workbench.views.environment.model.EnvironmentState;
import com.google.inject.Inject;

public class EnvironmentTab extends DelayLoadWorkbenchTab<EnvironmentPresenter>
{
 public interface Binder extends CommandBinder<Commands, EnvironmentTab.Shim> {}
   
   public abstract static class Shim
         extends DelayLoadTabShim<EnvironmentPresenter, EnvironmentTab>
         implements OpenDataFileHandler
   {
      @Handler
      public abstract void onLoadWorkspace();
      @Handler
      public abstract void onSaveWorkspace();
      @Handler
      public abstract void onImportDatasetFromFile();
      @Handler
      public abstract void onImportDatasetFromURL();
      @Handler
      public abstract void onClearWorkspace();

      abstract void initialize(EnvironmentState environmentState);
   }

   @Inject
   public EnvironmentTab(final Shim shim,
                         Binder binder,
                         EventBus events,
                         Commands commands,
                         Session session)
   {
      super("Environment", shim);
      binder.bind(commands, shim);
    
      session_ = session;
      
      events.addHandler(SessionInitEvent.TYPE, new SessionInitHandler() {
         
         public void onSessionInit(SessionInitEvent sie)
         {
            EnvironmentState environmentState = 
                  session_.getSessionInfo().getEnvironmentState();
            shim.initialize(environmentState);
         }
      });
   }
   
   private final Session session_;
}
