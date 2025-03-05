import {useReducer} from 'react';
import './App.css';



function App() {

const reducer = useReducer(ZustandsReducer, startZustand)
  return (
    <div className="App">
      <header className="App-header">

        <p>
          Heval Özdemir Ekici
        </p>

      </header>
    </div>
  );
}

export default App;
