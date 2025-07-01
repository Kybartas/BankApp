import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';

import { testDataService } from "../service/testDataService";
import { statementService } from "../service/statementService";
import { Account } from "../types/types";

import ImportCSVButton from '../components/ImportCSVButton';
import '../styles/dashboard.css';
import {useNotifications} from "../hooks/useNotifications";
import LoadingDots from "../components/LoadingDots";
import '../styles/base.css';

const HomeDashboard = () => {

    const [accounts, setAccounts] = useState<Account[]>([]);
    const [loading, setLoading] = useState(false);

    const { addNotification } = useNotifications();

    useEffect(() => {

        const getAccounts = async () => {

            setLoading(true);
            const data = await testDataService.getAccounts({ log: addNotification });
            setAccounts(data);
            setLoading(false);
        };

        getAccounts();

    }, [addNotification]);

    const handleImportCSV = async (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        if(!file) return;

        setLoading(true);
        await statementService.importCSV(file, { log: addNotification });
        setLoading(false);
    };

    return (
        <div className="dashboard-container">

            <h1>Home dashboard</h1>

            <div className="information-container">
                <ImportCSVButton onImport={ handleImportCSV }/>
            </div>


            {loading ? (<LoadingDots/>) : accounts.length === 0 ? (<p>No accounts found</p>) : (

                <div className="data-container">

                    <table className="data-table">
                        <thead>
                            <tr>
                                <th>Account Number</th>
                                <th>Balance</th>
                            </tr>
                        </thead>
                        <tbody>
                            {accounts.map((account) => (
                                <tr key={account.id}>
                                    <td>
                                        <Link className="account-dashboard-link" to={`/${account.accountNumber}`}>
                                            {account.accountNumber}
                                        </Link>
                                    </td>
                                    <td className={account.balance >= 0 ? "positive" : "negative"}>
                                        {account.balance}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>

                </div>

            )}

        </div>
    );
};

export default HomeDashboard;