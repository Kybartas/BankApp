import React, { useRef } from 'react';

interface ImportCSVButtonProps {
    onImport: (event: React.ChangeEvent<HTMLInputElement>) => void;
}

const ImportCSVButton: React.FC<ImportCSVButtonProps> = ({ onImport }) => {
    const fileInputRef = useRef<HTMLInputElement>(null);

    const handleButtonClick = () => {
        fileInputRef.current?.click();
    };

    return (
        <>
            <input
                type="file"
                accept=".csv"
                ref={fileInputRef}
                style={{ display: 'none' }}
                onChange={onImport}
            />
            <button className="button" onClick={handleButtonClick}>
                Import CSV
            </button>
        </>
    );
};

export default ImportCSVButton; 