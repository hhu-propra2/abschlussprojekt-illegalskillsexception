import Axios from "axios";
import {
    TRANSACTION_GETALL_BORROW,
    TRANSACTION_GETALL_LEND,
    TRANSACTION_PROBLEM,
    TRANSACTION_FINISHED
} from "../urlConstants";

export async function getAllTransactionsBackend(
    token,
    urlBorrow = TRANSACTION_GETALL_BORROW,
    urlLend = TRANSACTION_GETALL_LEND
) {
    let dataBorrow = await Axios.get(urlBorrow, {
        headers: {
            Authorization: token
        }
    });

    let dataLend = await Axios.get(urlLend, {
        headers: {
            Authorization: token
        }
    });
    return { borrow: dataBorrow.data.data, lend: dataLend.data.data };
}

export async function postTransactionFinishedBackend(
    id,
    token,
    url = TRANSACTION_FINISHED
) {
    let data = await Axios.post(
        url,
        {
            transactionId: id,
            isFaulty: false,
            itemReturn: true
        },
        {
            headers: {
                Authorization: token
            }
        }
    );

    return data;
}

export async function postTransactionProblemBackend(
    id,
    token,
    url = TRANSACTION_PROBLEM
) {
    let data = await Axios.post(
        url,
        {
            transactionId: id,
            isFaulty: true,
            itemReturn: true
        },
        {
            headers: {
                Authorization: token
            }
        }
    );

    return data;
}
