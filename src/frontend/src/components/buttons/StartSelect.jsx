const StartSelect = ({ onSelect }) => {
  return (
    <div className="start-select">
      <div className="select" onClick={onSelect}></div>
      <div className="start"></div>
    </div>
  );
};

export default StartSelect;
