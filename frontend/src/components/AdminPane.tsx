import React, { useState } from 'react';
import "../styles/admin.css"
import {testDataService} from "../api";
import {useNotifications} from "../hooks/useNotifications";
import "./LoadingDots"
import LoadingDots from "./LoadingDots";

const AdminPane = () => {

    const [showPane, setSetShowPane] = useState<boolean>(false);
    const [loading, setLoading] = useState<boolean>(false);

    const { addNotification } = useNotifications();

    const handleShowPane = () => {
        setSetShowPane(!showPane);
    }

    const handlePopulateDB = async () => {

        setLoading(true);
        await testDataService.populateDB({ log: addNotification });
        setLoading(false);
    };

    return (
        !showPane ?
                <button className="show-pane-button" onClick={ handleShowPane }>
                    {'>'}
                </button>
            : (

                <div className="pane-container">

                    <h1>Admin panel</h1>

                    <button className="close-pane-button" onClick={ handleShowPane }>
                        {'<'}
                    </button>

                    <button className="button" onClick={ handlePopulateDB } >
                        {'Populate database'}
                    </button>

                    {loading ? <LoadingDots/> : ""}

                </div>
            )
    )
};

export default AdminPane;