const { app, BrowserWindow } = require('electron');
const { spawn } = require('child_process');
const path = require('path');

let backendProcess;

function startNativeApp() {
  const backendPath = path.join(process.resourcesPath, 'myapp');

  backendProcess = spawn(backendPath, [], {
    stdio: 'inherit'
  });

  backendProcess.on('exit', (code) => {
    console.log("Native App exited with code:", code);
  });
}

function createWindow() {
  const win = new BrowserWindow({
    width: 1000,
    height: 800,
  });

  // Load the SPA served by the native image
  win.loadURL("http://localhost:8080/");
}

app.whenReady().then(() => {
  startNativeApp();
  setTimeout(createWindow, 800);  // slight delay to let server boot
});

app.on('before-quit', () => {
  if (backendProcess) backendProcess.kill();
});

