export const ADD_TRANSACTION_ITEM = "ADD_TRANSACTION_ITEM";
export const SET_TRANSACTION_ITEMS = "SET_TRANSACTION_ITEMS";
export const UPDATE_TRANSACTION_ITEM = "UPDATE_TRANSACTION_ITEMS";

export function getAddTransactionItemAction(item) {
    return {
        type: ADD_TRANSACTION_ITEM,
        data: item
    };
}

export function getSetTransactionItemListAction(list) {
    return {
        type: SET_TRANSACTION_ITEMS,
        list: list
    };
}

export function getUpdateTransactionItemAction(item) {
    return {
        type: UPDATE_TRANSACTION_ITEM,
        data: item
    };
}