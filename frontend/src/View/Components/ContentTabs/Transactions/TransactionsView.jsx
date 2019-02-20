import React from "react";
import TransactionsItem from "./Transactions/TransactionsItem";

import { connect } from "react-redux";
import { getAllTransaction } from "../../../../Services/Transaction/transactionCompleteService";

const mapStateToProps = state => {
    return { items: state.transactionstore };
};

export class TransactionsView extends React.Component {
    async componentDidMount() {
        await getAllTransaction();
        console.log("Transaction onload list", this.props.items);
    }

    render() {
        return (
            <>
                <div id="testtext" className="grid-article-view">
                    {this.props.items.map(dataItem => (
                        <TransactionsItem key={dataItem.id} data={dataItem} />
                    ))}
                </div>
            </>
        );
    }
}

let transactionView = connect(
    mapStateToProps,
    null,
    null
)(TransactionsView);
export default transactionView;
