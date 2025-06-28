import React, { useState } from 'react';
import "../styles/admin.css"
import {testDataService} from "../api";
import {useNotifications} from "../hooks/useNotifications";
import LoadingDots from "./LoadingDots";

const AdminPane = () => {

    const [loading, setLoading] = useState<boolean>(false);

    const { addNotification } = useNotifications();

    const handlePopulateDB = async () => {

        setLoading(true);
        await testDataService.populateDB({ log: addNotification });
        setLoading(false);
    };

    return (
        <div className="pane-container">

            <h1>Admin panel</h1>

            <button className="pane-button" onClick={handlePopulateDB}>
                {'Populate database'}
            </button>


            {loading ? <LoadingDots/> : ""}

        </div>
    )
};

export default AdminPane;