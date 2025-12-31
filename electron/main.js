const { app, BrowserWindow, Menu } = require('electron');
const { spawn } = require('child_process');
const path = require('path');

let backendProcess;
let mainWindow;

function startBackend() {
  if (backendProcess) {
    console.log("Backend already running");
    return;
  }

  const backendPath = path.join(process.resourcesPath, 'myapp');
  console.log("Starting backend from:", backendPath);

  backendProcess = spawn(backendPath, [], {
    stdio: 'inherit'
  });

  backendProcess.on('exit', (code) => {
    console.log("Native App exited with code:", code);
    backendProcess = null;
    updateMenu();
  });

  backendProcess.on('error', (err) => {
    console.error('Failed to start backend:', err);
    backendProcess = null;
    updateMenu();
  });

  updateMenu();

  // Reload page after a delay to ensure backend is up
  if (mainWindow) {
    setTimeout(() => mainWindow.reload(), 1000);
  }
}

function stopBackend() {
  if (backendProcess) {
    backendProcess.kill();
    backendProcess = null;
    updateMenu();
    console.log("Backend stopped");
  }
}

function createMenu() {
  const template = [
    {
      label: 'Service',
      submenu: [
        {
          label: 'Start Backend',
          click: startBackend,
          enabled: !backendProcess
        },
        {
          label: 'Stop Backend',
          click: stopBackend,
          enabled: !!backendProcess
        },
        { type: 'separator' },
        { role: 'quit' }
      ]
    },
    {
      label: 'View',
      submenu: [
        { role: 'reload' },
        { role: 'forceReload' },
        { role: 'toggleDevTools' }
      ]
    }
  ];

  const menu = Menu.buildFromTemplate(template);
  Menu.setApplicationMenu(menu);
}

function updateMenu() {
  createMenu();
}

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1000,
    height: 800,
  });

  // Load the SPA served by the native image
  mainWindow.loadURL("http://localhost:8080/");

  mainWindow.on('closed', () => {
    mainWindow = null;
  });
}

app.whenReady().then(() => {
  createMenu();
  createWindow();
});

app.on('before-quit', () => {
  if (backendProcess) backendProcess.kill();
});

