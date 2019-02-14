import React from "react";


import TextBox from "react-uwp/TextBox";
import Button from "react-uwp/Button";

export default class LendItemComponentEditDialog extends React.Component {
    render() {
        return (
            <div>
                <label>Title:</label>
                <TextBox defaultValue={this.props.data.title}/>
                <label>Description</label>
                <TextBox defaultValue={this.props.data.description}/>
                <label>Safety Deposit</label>
                <TextBox defaultValue={this.props.data.deposit} type="number" />
                <label>Daily rate</label>
                <TextBox defaultValue={this.props.data.dailyRate} type="number" />
                <label>Place</label>
                <TextBox defaultValue={this.props.data.place} />
                <Button onClick={() => this.saveChanges()}>Confirm</Button>
                <Button onClick={() => this.props.close()}>Cancel</Button>
            </div>
        );
    }

    saveChanges(){

        this.props.close();
    }
}