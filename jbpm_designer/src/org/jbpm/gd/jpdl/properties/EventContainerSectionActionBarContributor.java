package org.jbpm.gd.jpdl.properties;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IActionBars;
import org.jbpm.gd.common.util.SharedImages;
import org.jbpm.gd.jpdl.Plugin;
import org.jbpm.gd.jpdl.command.ActionElementCreateCommand;
import org.jbpm.gd.jpdl.command.ActionElementDeleteCommand;
import org.jbpm.gd.jpdl.command.EventCreateCommand;
import org.jbpm.gd.jpdl.command.EventDeleteCommand;
import org.jbpm.gd.jpdl.model.ActionElementContainer;
import org.jbpm.gd.jpdl.model.EventContainer;


public class EventContainerSectionActionBarContributor implements IMenuCreator {
	
	private static final String ACTION_ID = "org.jbpm.gd.jpdl.action";
	private static final String CANCEL_TIMER_ID = "org.jbpm.gd.jpdl.cancelTimer";
	private static final String CREATE_TIMER_ID = "org.jbpm.gd.jpdl.createTimer";
	private static final String MAIL_ID = "org.jbpm.gd.jpdl.mail";
	private static final String SCRIPT_ID = "org.jbpm.gd.jpdl.script";
	
	private String elementToCreate = ACTION_ID;
	
	private EventContainerSection eventsSection;
	
	private ActionContributionItem addEventToolbarContributionItem;
	private ActionContributionItem addActionElementToolbarContributionItem;
	private ActionContributionItem removeToolbarContributionItem;
	private ActionContributionItem addEventMenuContributionItem;
	private ActionContributionItem addActionMenuContributionItem;
	private ActionContributionItem addCreateTimerMenuContributionItem;
	private ActionContributionItem addCancelTimerMenuContributionItem;
	private ActionContributionItem addScriptMenuContributionItem;
	private ActionContributionItem addMailMenuContributionItem;
	private ActionContributionItem removeMenuContributionItem;
	private Separator toolbarSeparator;
	private Separator firstMenuSeparator;
	private Separator secondMenuSeparator;
	private Menu addActionElementMenu;
	private Menu popupMenu;
	
	public EventContainerSectionActionBarContributor(EventContainerSection eventsSection) {
		this.eventsSection = eventsSection;
		addEventToolbarContributionItem = new ActionContributionItem(createAddEventAction());
		addActionElementToolbarContributionItem = new ActionContributionItem(createAddActionElementAction());
		removeToolbarContributionItem = new ActionContributionItem(createRemoveAction());
		addEventMenuContributionItem = new ActionContributionItem(createAddEventAction());
		addActionMenuContributionItem = new ActionContributionItem(createAddActionAction());
		addCreateTimerMenuContributionItem = new ActionContributionItem(createAddCreateTimerAction());
		addCancelTimerMenuContributionItem = new ActionContributionItem(createAddCancelTimerAction());
		addScriptMenuContributionItem = new ActionContributionItem(createAddScriptAction());
		addMailMenuContributionItem = new ActionContributionItem(createAddMailAction());
		removeMenuContributionItem = new ActionContributionItem(createRemoveAction());
		toolbarSeparator = new Separator();
		firstMenuSeparator = new Separator();
		secondMenuSeparator = new Separator();
	}
	
	private IAction createAddEventAction() {
		IAction action = new Action() {
			public void run() {
				addNewEvent();
			}
		};
		setEventInfo(action);
		return action;
	}
	
	private IAction createAddCancelTimerAction() {
		IAction action = new Action() {
			public void run() {
				addNewActionElement(CANCEL_TIMER_ID);
			}
		};
		setCancelTimerInfo(action);
		return action;
	}
	
	private IAction createAddActionAction() {
		IAction action = new Action() {
			public void run() {
				addNewActionElement(ACTION_ID);
			}
		};
		setActionInfo(action);
		return action;
	}
	
	private IAction createAddCreateTimerAction() {
		IAction action = new Action() {
			public void run() {
				addNewActionElement(CREATE_TIMER_ID);
			}
		};
		setCreateTimerInfo(action);
		return action;
	}
	
	private IAction createAddScriptAction() {
		IAction action = new Action() {
			public void run() {
				addNewActionElement(SCRIPT_ID);
			}
		};
		setScriptInfo(action);
		return action;
	}
	
	private IAction createAddMailAction() {
		IAction action = new Action() {
			public void run() {
				addNewActionElement(MAIL_ID);
			}
		};
		setMailInfo(action);
		return action;
	}
	
	private IAction createAddActionElementAction() {
		IAction action = new Action() {
			public void run() {
				addNewActionElement(elementToCreate);
			}
		};
		action.setMenuCreator(this);
		setActionInfo(action);
		return action;
	}
	
	private void setEventInfo(IAction action) {
		action.setImageDescriptor(
				ImageDescriptor.createFromURL(Plugin.getDefault()
						.getBundle().getEntry("/icons/full/obj16/new_event_enabled.gif")));
		action.setDisabledImageDescriptor(
				ImageDescriptor.createFromURL(Plugin.getDefault()
						.getBundle().getEntry("/icons/full/obj16/new_event_disabled.gif")));
		action.setToolTipText("添加事件");	
		action.setText("新建事件");
	}

	private void setActionInfo(IAction action) {
		action.setImageDescriptor(
				ImageDescriptor.createFromURL(Plugin.getDefault()
						.getBundle().getEntry("/icons/full/obj16/new_action_enabled.gif")));
		action.setDisabledImageDescriptor(
				ImageDescriptor.createFromURL(Plugin.getDefault()
						.getBundle().getEntry("/icons/full/obj16/new_action_disabled.gif")));
		action.setToolTipText("添加动作");	
		action.setText("新建动作");
	}
	
	private void setCancelTimerInfo(IAction action) {
		action.setImageDescriptor(
				ImageDescriptor.createFromURL(Plugin.getDefault()
						.getBundle().getEntry("/icons/full/obj16/new_cancel_timer_enabled.gif")));
		action.setDisabledImageDescriptor(
				ImageDescriptor.createFromURL(Plugin.getDefault()
						.getBundle().getEntry("/icons/full/obj16/new_cancel_timer_disabled.gif")));
		action.setToolTipText("添加取消定时器");	
		action.setText("新建取消定时器");
	}
	
	private void setCreateTimerInfo(IAction action) {
		action.setImageDescriptor(
				ImageDescriptor.createFromURL(Plugin.getDefault()
						.getBundle().getEntry("/icons/full/obj16/new_create_timer_enabled.gif")));
		action.setDisabledImageDescriptor(
				ImageDescriptor.createFromURL(Plugin.getDefault()
						.getBundle().getEntry("/icons/full/obj16/new_create_timer_disabled.gif")));
		action.setToolTipText("添加创建定时器");	
		action.setText("新建创建定时器");
	}
	
	private void setMailInfo(IAction action) {
		action.setImageDescriptor(
				ImageDescriptor.createFromURL(Plugin.getDefault()
						.getBundle().getEntry("/icons/full/obj16/new_mail_enabled.gif")));
		action.setDisabledImageDescriptor(
				ImageDescriptor.createFromURL(Plugin.getDefault()
						.getBundle().getEntry("/icons/full/obj16/new_mail_disabled.gif")));
		action.setToolTipText("添加邮件");	
		action.setText("新建邮件");
	}
	
	private void setScriptInfo(IAction action) {
		action.setImageDescriptor(
				ImageDescriptor.createFromURL(Plugin.getDefault()
						.getBundle().getEntry("/icons/full/obj16/new_script_enabled.gif")));
		action.setDisabledImageDescriptor(
				ImageDescriptor.createFromURL(Plugin.getDefault()
						.getBundle().getEntry("/icons/full/obj16/new_script_disabled.gif")));
		action.setToolTipText("添加脚本");	
		action.setText("新建脚本");
	}
	
	private IAction createRemoveAction() {
		IAction action = new Action() {
			public void run() {
				removeSelectedObject();
			}
		};
		setRemoveInfo(action);
		action.setEnabled(false);
		return action;
	}

	private void setRemoveInfo(IAction action) {
		action.setImageDescriptor(
				ImageDescriptor.createFromURL(Plugin.getDefault()
						.getBundle().getEntry("/icons/full/obj16/delete_enabled.gif")));
		action.setDisabledImageDescriptor(
				ImageDescriptor.createFromURL(Plugin.getDefault()
						.getBundle().getEntry("/icons/full/obj16/delete_disabled.gif")));
		action.setToolTipText("移除选中对象");
		action.setText("删除");
	}
	
	private void createActionElementMenu(Control parent) {
		addActionElementMenu = new Menu(parent);
		createAddActionElementMenuItems(addActionElementMenu);
	}

	private void createAddActionElementMenuItems(Menu menu) {
		boolean enabled = eventsSection.getSelectedEvent() != null;
		createAddActionMenuItem(menu, enabled);
		createAddScriptMenuItem(menu, enabled);
		createAddCreateTimerMenuItem(menu, enabled);
		createAddCancelTimerMenuItem(menu, enabled);
		createAddMailMenuItem(menu, enabled);
	}

	private void createAddActionMenuItem(Menu menu, boolean enabled) {
		MenuItem addActionMenuItem = new MenuItem(menu, SWT.PUSH);
		addActionMenuItem.setText("新建动作");
		ImageDescriptor enabledDescriptor = ImageDescriptor.createFromURL(Plugin.getDefault()
				.getBundle().getEntry("/icons/full/obj16/new_action_enabled.gif"));
		addActionMenuItem.setImage(SharedImages.INSTANCE.getImage(enabledDescriptor));
		addActionMenuItem.setEnabled(enabled);
		if (enabled) {
			addActionMenuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					addNewActionElement(ACTION_ID);
					if (((MenuItem)e.widget).getParent() != popupMenu) {
						elementToCreate = ACTION_ID;
						setActionInfo(addActionElementToolbarContributionItem.getAction());
					}
				}			
			});
		}
	}
	
	private void createAddCancelTimerMenuItem(Menu menu, boolean enabled) {
		MenuItem addCancelTimerMenuItem = new MenuItem(menu, SWT.PUSH);
		addCancelTimerMenuItem.setText("新建取消定时器");
		ImageDescriptor enabledDescriptor = ImageDescriptor.createFromURL(Plugin.getDefault()
				.getBundle().getEntry("/icons/full/obj16/new_cancel_timer_enabled.gif"));
		addCancelTimerMenuItem.setImage(SharedImages.INSTANCE.getImage(enabledDescriptor));
		addCancelTimerMenuItem.setEnabled(enabled);
		if (enabled) {
			addCancelTimerMenuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					addNewActionElement(CANCEL_TIMER_ID);
					if (((MenuItem)e.widget).getParent() != popupMenu) {
						elementToCreate = CANCEL_TIMER_ID;
						setCancelTimerInfo(addActionElementToolbarContributionItem.getAction());
					}
				}			
			});
		}		
	}
	
	private void createAddCreateTimerMenuItem(Menu menu, boolean enabled) {
		MenuItem addCreateTimerMenuItem = new MenuItem(menu, SWT.PUSH);
		addCreateTimerMenuItem.setText("新建创建定时器");
		ImageDescriptor enabledDescriptor = ImageDescriptor.createFromURL(Plugin.getDefault()
				.getBundle().getEntry("/icons/full/obj16/new_create_timer_enabled.gif"));
		addCreateTimerMenuItem.setImage(SharedImages.INSTANCE.getImage(enabledDescriptor));
		addCreateTimerMenuItem.setEnabled(enabled);
		if (enabled) {
			addCreateTimerMenuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					addNewActionElement(CREATE_TIMER_ID);
					if (((MenuItem)e.widget).getParent() != popupMenu) {
						elementToCreate = CREATE_TIMER_ID;
						setCreateTimerInfo(addActionElementToolbarContributionItem.getAction());
					}
				}			
			});
		}		
	}
	
	private void createAddMailMenuItem(Menu menu, boolean enabled) {
		MenuItem addMailMenuItem = new MenuItem(menu, SWT.PUSH);
		addMailMenuItem.setText("新建邮件");
		ImageDescriptor enabledDescriptor = ImageDescriptor.createFromURL(Plugin.getDefault()
				.getBundle().getEntry("/icons/full/obj16/new_mail_enabled.gif"));
		addMailMenuItem.setImage(SharedImages.INSTANCE.getImage(enabledDescriptor));
		addMailMenuItem.setEnabled(enabled);
		if (enabled) {
			addMailMenuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					addNewActionElement(MAIL_ID);
					if (((MenuItem)e.widget).getParent() != popupMenu) {
						elementToCreate = MAIL_ID;
						setMailInfo(addActionElementToolbarContributionItem.getAction());
					}
				}			
			});
		}		
	}
	
	private void createAddScriptMenuItem(Menu menu, boolean enabled) {
		MenuItem addScriptMenuItem = new MenuItem(menu, SWT.PUSH);
		addScriptMenuItem.setText("新建脚本");
		ImageDescriptor enabledDescriptor = ImageDescriptor.createFromURL(Plugin.getDefault()
				.getBundle().getEntry("/icons/full/obj16/new_script_enabled.gif"));
		addScriptMenuItem.setImage(SharedImages.INSTANCE.getImage(enabledDescriptor));
		addScriptMenuItem.setEnabled(enabled);
		if (enabled) {
			addScriptMenuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					addNewActionElement(SCRIPT_ID);
					if (((MenuItem)e.widget).getParent() != popupMenu) {
						elementToCreate = SCRIPT_ID;
						setScriptInfo(addActionElementToolbarContributionItem.getAction());
					}
				}			
			});
		}		
	}
	
	private void addNewActionElement(String elementToCreate) {
		ActionElementContainer target = eventsSection.getSelectedEvent();
		ActionElementCreateCommand command = new ActionElementCreateCommand(target.getFactory());
		command.setActionElementContainer(target);
		command.setActionId(elementToCreate);
		getCommandStack().execute(command);
	}
	
	private void addNewEvent() {
		EventContainer target = eventsSection.getEventContainer();
		EventCreateCommand command = new EventCreateCommand(target.getFactory());
		command.setEventContainer(target);
		getCommandStack().execute(command);
	}
	
	private void removeSelectedObject() {
		if (eventsSection.getSelectedActionElement() != null) {
			ActionElementDeleteCommand command = new ActionElementDeleteCommand();
			command.setActionElement(eventsSection.getSelectedActionElement());
			command.setActionElementContainer(eventsSection.getSelectedEvent());
			getCommandStack().execute(command);
		} else {
			EventDeleteCommand command = new EventDeleteCommand();
			command.setEventContainer(eventsSection.getEventContainer());
			command.setEvent(eventsSection.getSelectedEvent());
			getCommandStack().execute(command);
		}
	}
	
	private CommandStack getCommandStack() {
		return eventsSection.getCommandStack();
	}
	
	private IActionBars getActionBars() {
		return eventsSection.getTabbedPropertySheetPage().getSite().getActionBars();
	}
	
	public void setRemoveEnabled(boolean enabled) {
		removeToolbarContributionItem.getAction().setEnabled(enabled);
		removeMenuContributionItem.getAction().setEnabled(enabled);
	}
	
	public void setAddActionElementEnabled(boolean enabled) {
		addActionElementToolbarContributionItem.getAction().setEnabled(enabled);
		addActionMenuContributionItem.getAction().setEnabled(enabled);
		addCreateTimerMenuContributionItem.getAction().setEnabled(enabled);
		addCancelTimerMenuContributionItem.getAction().setEnabled(enabled);
		addScriptMenuContributionItem.getAction().setEnabled(enabled);
		addMailMenuContributionItem.getAction().setEnabled(enabled);
	}

	public void activateContributions() {
//		getActionBars().getToolBarManager().add(addEventToolbarContributionItem);
//		getActionBars().getToolBarManager().add(addActionElementToolbarContributionItem);
//		getActionBars().getToolBarManager().add(toolbarSeparator);
//		getActionBars().getToolBarManager().add(removeToolbarContributionItem);
		getActionBars().getMenuManager().add(addEventMenuContributionItem);
		getActionBars().getMenuManager().add(firstMenuSeparator);
		getActionBars().getMenuManager().add(addActionMenuContributionItem);
		getActionBars().getMenuManager().add(addScriptMenuContributionItem);
		getActionBars().getMenuManager().add(addCreateTimerMenuContributionItem);
		getActionBars().getMenuManager().add(addCancelTimerMenuContributionItem);
		getActionBars().getMenuManager().add(addMailMenuContributionItem);
		getActionBars().getMenuManager().add(secondMenuSeparator);
		getActionBars().getMenuManager().add(removeMenuContributionItem);
		getActionBars().updateActionBars();
	}

	public void deactivateContributions() {
//		getActionBars().getToolBarManager().remove(addEventToolbarContributionItem);
//		getActionBars().getToolBarManager().remove(addActionElementToolbarContributionItem);
//		getActionBars().getToolBarManager().remove(toolbarSeparator);
//		getActionBars().getToolBarManager().remove(removeToolbarContributionItem);
		getActionBars().getMenuManager().remove(addEventMenuContributionItem);
		getActionBars().getMenuManager().remove(firstMenuSeparator);
		getActionBars().getMenuManager().remove(addActionMenuContributionItem);
		getActionBars().getMenuManager().remove(addCreateTimerMenuContributionItem);
		getActionBars().getMenuManager().remove(addCancelTimerMenuContributionItem);
		getActionBars().getMenuManager().remove(addScriptMenuContributionItem);
		getActionBars().getMenuManager().remove(addMailMenuContributionItem);
		getActionBars().getMenuManager().remove(secondMenuSeparator);
		getActionBars().getMenuManager().remove(removeMenuContributionItem);
		getActionBars().updateActionBars();
	}

	public void createPopupMenu(Composite composite) {
		popupMenu = new Menu(composite);
		composite.setMenu(popupMenu);
		popupMenu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				disposePopupMenu();
				createPopupMenu();
			}			
		});
	}
	
	private void disposePopupMenu() {
		MenuItem[] menuItems = popupMenu.getItems();
		for (int i = 0; i < menuItems.length; i++) {
			menuItems[i].dispose();
		}
	}
	
	private void createPopupMenu() {
		createAddEventMenuItem();
		new MenuItem(popupMenu, SWT.SEPARATOR);
		createAddActionElementMenuItems(popupMenu);
		new MenuItem(popupMenu, SWT.SEPARATOR);
		createRemoveMenuItem();
	}
	
	private void createAddEventMenuItem() {		
		MenuItem addEventItem = new MenuItem(popupMenu, SWT.PUSH);
		addEventItem.setText("新建事件");
		ImageDescriptor descriptor = ImageDescriptor.createFromURL(Plugin.getDefault()
				.getBundle().getEntry("/icons/full/obj16/new_event_enabled.gif"));
		addEventItem.setImage(SharedImages.INSTANCE.getImage(descriptor));
		addEventItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addNewEvent();
			}			
		});
	}
	
	private String getDeleteImagePath() {
		String imagePath;
		if (hasSelection()) { 
			imagePath = "/icons/full/obj16/delete_enabled.gif";
		} else {
			imagePath = "/icons/full/obj16/delete_disabled.gif";
		}
		return imagePath;
	}

	private void createRemoveMenuItem() {
		MenuItem removeItem = new MenuItem(popupMenu, SWT.PUSH);
		removeItem.setText("删除");
		ImageDescriptor descriptor = ImageDescriptor.createFromURL(Plugin.getDefault()
				.getBundle().getEntry(getDeleteImagePath()));
		removeItem.setImage(SharedImages.INSTANCE.getImage(descriptor));
		removeItem.setEnabled(hasSelection());
		removeItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeSelectedObject();
			}
		});
	}
	
	private boolean hasSelection() {
		return eventsSection.getSelectedActionElement() != null || eventsSection.getSelectedEvent() != null;
	}

	public void dispose() {
		if (addActionElementMenu != null) {
			MenuItem[] items = addActionElementMenu.getItems();
			for (int i = 0; i < items.length; i++) {
				items[i].dispose();
			}
		}
	}

	public Menu getMenu(Control parent) {
		if (addActionElementMenu == null) {
			createActionElementMenu(parent);
		}
		return addActionElementMenu;
	}

	public Menu getMenu(Menu parent) {
		return null;
	}
	
	
}
