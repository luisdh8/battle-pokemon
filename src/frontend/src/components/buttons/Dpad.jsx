const Dpad = ({ onUp, onDown, onLeft, onRight }) => {
  return (
    <div className="dpad">
      <div></div>
      <div className="dpad-button up" onClick={onUp}></div>
      <div></div>
      <div className="dpad-button left" onClick={onLeft}></div>
      <div className="dpad-button center"></div>
      <div className="dpad-button right" onClick={onRight}></div>
      <div></div>
      <div className="dpad-button down" onClick={onDown}></div>
      <div></div>
    </div>
  );
};

export default Dpad;
