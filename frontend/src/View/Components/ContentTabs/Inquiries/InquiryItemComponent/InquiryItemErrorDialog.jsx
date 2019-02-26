import React from "react";

import Button from "react-uwp/Button";

import "./InquiryItemErrorDialog.css";

export default class InquiryItemErrorDialog extends React.Component {
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
                    <Button onClick={this.props.closeDialog}>Exit</Button>
                </div>
            </article>
        );
    }
}
