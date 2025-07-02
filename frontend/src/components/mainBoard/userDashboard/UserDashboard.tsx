import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Account } from "../../../types";
import '../mainDashboard.css';
import { useLogs } from "../../../hooks/useLogs";
import LoadingDots from "../../common/loadingDots/LoadingDots";
import '../../../styles/base.css';
import {userService} from "../../../service/userService";
import { useAuth } from "../../../hooks/useAuth";

const UserDashboard = () => {

    const [accounts, setAccounts] = useState<Account[]>([]);
    const [loading, setLoading] = useState(false);
    const { user } = useAuth();

    const { addLog } = useLogs();

    useEffect(() => {

        if (!user) {
            return;
        }

        const getAccounts = async () => {

            setLoading(true);
            const data = await userService.getAccounts(user.username, { log: addLog });
            setAccounts(data);
            setLoading(false);
        };

        getAccounts();

    }, [addLog, user]);

    const handleOpenAccount = async () => {

        if (!user) {
            return;
        }

        await userService.openNewAccount(user.username, { log: addLog });

    }

    return (
        <div className="dashboard-container">

            <h1> Bank User {user?.username} </h1>

            <div className="information-container">

                <button className="button" onClick={handleOpenAccount}>
                    Open new account
                </button>

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

export default UserDashboard;