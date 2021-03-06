import React from "react";
import NavigationView from "react-uwp/NavigationView";
import SplitViewCommand from "react-uwp/SplitViewCommand";
import Tabs, { Tab } from "react-uwp/Tabs";

import { store } from "../../../Store/reduxInit";
import ConsumerView from "../ContentTabs/Consumer/ConsumerView";
import TransactionsView from "../ContentTabs/Transactions/TransactionsView";
import InquiriesView from "../ContentTabs/Inquiries/InquiriesView";
import ConflictView from "../ContentTabs/Conflict/ConflictView";
import UserView from "../ContentTabs/User/UserView";

import Toast from "react-uwp/Toast";

import "./ContentPage.css";
import { getAllOverdueTransactions } from "../../../Services/User/authentificationCompleteService.js";
import { logOutUser } from "../../../Services/User/authentificationCompleteService";
import OwnerView from "../ContentTabs/Owner/OwnerView";
import { setAdmin } from "../../../Services/Conflict/conflictCompleteService";
import { connect } from "react-redux";

class ContentPage extends React.Component {
    constructor(props) {
        super(props);

        this.tabs = React.createRef();
        this.navigation = React.createRef();

        this.state = { showNotifToast: false };
    }

    async componentDidMount() {
        setAdmin();
        let data = await getAllOverdueTransactions(store.getState().user.token);
        let list = data.data.data;
        if (list.length !== 0) {
            this.setState({ showNotifToast: true });
        }
    }

    switchTab(index) {
        this.navigation.current.setState({ expanded: false });
        this.tabs.current.setState({ tabFocusIndex: index });
    }

    renderNavigation() {
        const nodeList = [
            <SplitViewCommand
                onClick={() => this.switchTab(0)}
                label="Market"
                icon={"\uECCD"}
            />,
            <SplitViewCommand
                onClick={() => this.switchTab(1)}
                label="Your Items"
                icon={"\uF0AD"}
            />,
            <SplitViewCommand
                onClick={() => this.switchTab(2)}
                label="Inquiry"
                icon={"\uE73E"}
            />,
            <SplitViewCommand
                onClick={() => this.switchTab(3)}
                label="Transactions"
                icon={"\uE9F5"}
            />
        ];

        if (this.props.user.admin) {
            nodeList.push(
                <SplitViewCommand
                    onClick={() => this.switchTab(4)}
                    label="Conflicts"
                    icon={"Admin"}
                />
            );
        }
        return nodeList;
    }

    render() {
        return (
            <>
                <Toast
                    defaultShow={this.state.showNotifToast}
                    onToggleShowToast={showNotifToast =>
                        this.setState({ showNotifToast })
                    }
                    title="You have articles that are due to return."
                    description={
                        "View the transaction tab for more informations."
                    }
                    showCloseIcon
                />

                <NavigationView
                    expandedWidth={200}
                    focusNavigationNodeIndex={0}
                    ref={this.navigation}
                    navigationTopNodes={this.renderNavigation()}
                    navigationBottomNodes={[
                        <SplitViewCommand
                            onClick={() => this.switchTab(5)}
                            label="Profile"
                            icon={"Contact"}
                        />,
                        <SplitViewCommand
                            onClick={() => logOutUser()}
                            label="LogOut"
                            icon={"PowerButton"}
                        />
                    ]}
                    displayMode="compact"
                    autoResize={false}
                    defaultExpanded={true}
                >
                    <Tabs
                        tabTitleStyle={{ display: "none" }}
                        ref={this.tabs}
                        id="content-view"
                        animateMode="in"
                        style={{ display: "block" }}
                    >
                        <Tab
                            title="Borrow"
                            style={{ width: "100%", height: "100%" }}
                        >
                            <ConsumerView />
                        </Tab>
                        <Tab
                            title="Lend"
                            style={{ width: "100%", height: "100%" }}
                        >
                            <OwnerView />
                        </Tab>
                        <Tab
                            title="Inquiries"
                            style={{ width: "100%", height: "100%" }}
                        >
                            <InquiriesView />
                        </Tab>
                        <Tab style={{ width: "100%", height: "100%" }}>
                            <TransactionsView />
                        </Tab>
                        <Tab style={{ width: "100%", height: "100%" }}>
                            <ConflictView />
                        </Tab>
                        <Tab style={{ width: "100%", height: "100%" }}>
                            <UserView />
                        </Tab>
                    </Tabs>
                </NavigationView>
            </>
        );
    }
}

// start of code change
const mapStateToProps = state => {
    return { user: state.user };
};

export default connect(mapStateToProps)(ContentPage);
