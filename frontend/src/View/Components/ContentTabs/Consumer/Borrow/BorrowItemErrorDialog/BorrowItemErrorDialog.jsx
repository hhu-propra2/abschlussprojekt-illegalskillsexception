import React from "react";

import Button from "react-uwp/Button";
import "./BorrowItemErrorDialog.css";

export default class BorrowItemAcceptanceDialog extends React.Component {
    render() {
        return (
            <article className="error-dialog">
                <h3>Sorry, an error occurred.</h3>
                <div>
                    <div>
                        <span>
                            {this.props.errorMessage}
                        </span>
                    </div>
                    <Button onClick={this.props.closeDialog}>Cancel</Button>
                </div>
            </article>
        );
    }
}
