
import React from 'react';

function App() {
  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center">
      <div className="max-w-md mx-auto bg-white rounded-lg shadow-md p-8">
        <h1 className="text-2xl font-bold text-center text-gray-800 mb-4">
          HerSafeZone
        </h1>
        <p className="text-gray-600 text-center mb-6">
          This is a placeholder web interface. The actual Android app files are in the project structure for reference.
        </p>
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
          <h2 className="text-lg font-semibold text-blue-800 mb-2">Android App Structure</h2>
          <ul className="text-sm text-blue-700 space-y-1">
            <li>• OnboardingScreen.kt</li>
            <li>• HomeScreen.kt</li>
            <li>• LiveMapScreen.kt</li>
            <li>• SosService.kt</li>
            <li>• GlobalListener.kt</li>
            <li>• PresenceWorker.kt</li>
          </ul>
        </div>
      </div>
    </div>
  );
}

export default App;
